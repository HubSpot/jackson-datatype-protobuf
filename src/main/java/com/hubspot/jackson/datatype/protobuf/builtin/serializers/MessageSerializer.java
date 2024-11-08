package com.hubspot.jackson.datatype.protobuf.builtin.serializers;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.util.NameTransformer;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.ExtensionRegistry.ExtensionInfo;
import com.google.protobuf.Message;
import com.google.protobuf.MessageOrBuilder;
import com.hubspot.jackson.datatype.protobuf.ExtensionRegistryWrapper;
import com.hubspot.jackson.datatype.protobuf.ProtobufJacksonConfig;
import com.hubspot.jackson.datatype.protobuf.ProtobufSerializer;
import com.hubspot.jackson.datatype.protobuf.internal.MessageSchemaGenerator;
import com.hubspot.jackson.datatype.protobuf.internal.PropertyNamingCache;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class MessageSerializer extends ProtobufSerializer<MessageOrBuilder> {

  private final boolean unwrappingSerializer;
  private final Map<Descriptor, PropertyNamingCache> propertyNamingCache;

  /**
   * @deprecated use {@link #MessageSerializer(ProtobufJacksonConfig)} instead
   */
  @Deprecated
  public MessageSerializer(ExtensionRegistryWrapper extensionRegistry) {
    this(ProtobufJacksonConfig.builder().extensionRegistry(extensionRegistry).build());
  }

  public MessageSerializer(ProtobufJacksonConfig config) {
    this(config, false);
  }

  private MessageSerializer(ProtobufJacksonConfig config, boolean unwrappingSerializer) {
    super(MessageOrBuilder.class, config);
    this.unwrappingSerializer = unwrappingSerializer;
    this.propertyNamingCache = new ConcurrentHashMap<>();
  }

  @Override
  public void serialize(
    MessageOrBuilder message,
    JsonGenerator generator,
    SerializerProvider serializerProvider
  ) throws IOException {
    if (!isUnwrappingSerializer()) {
      generator.writeStartObject();
    }

    boolean proto3 = message
      .getDescriptorForType()
      .getFile()
      .toProto()
      .getSyntax()
      .contentEquals("proto3");
    Include include = serializerProvider
      .getConfig()
      .getDefaultPropertyInclusion()
      .getValueInclusion();
    boolean writeDefaultValues = proto3 && include != Include.NON_DEFAULT;
    boolean writeEmptyCollections =
      include != Include.NON_DEFAULT && include != Include.NON_EMPTY;

    Function<FieldDescriptor, String> propertyNaming = getPropertyNaming(
      message,
      serializerProvider
    );
    Descriptor descriptor = message.getDescriptorForType();
    List<FieldDescriptor> fields = descriptor.getFields();
    if (descriptor.isExtendable()) {
      fields = new ArrayList<>(fields);

      for (ExtensionInfo extensionInfo : getConfig()
        .extensionRegistry()
        .getExtensionsByDescriptor(descriptor)) {
        fields.add(extensionInfo.descriptor);
      }
    }

    for (FieldDescriptor field : fields) {
      String fieldName = propertyNaming.apply(field);

      if (field.isRepeated()) {
        List<?> valueList = (List<?>) message.getField(field);

        if (!valueList.isEmpty() || writeEmptyCollections) {
          if (field.isMapField()) {
            generator.writeFieldName(fieldName);
            writeMap(field, valueList, generator, serializerProvider);
          } else if (
            valueList.size() == 1 && writeSingleElementArraysUnwrapped(serializerProvider)
          ) {
            generator.writeFieldName(fieldName);
            writeValue(field, valueList.get(0), generator, serializerProvider);
          } else {
            generator.writeArrayFieldStart(fieldName);
            for (Object subValue : valueList) {
              writeValue(field, subValue, generator, serializerProvider);
            }
            generator.writeEndArray();
          }
        }
      } else if (
        message.hasField(field) ||
        (writeDefaultValues &&
          !supportsFieldPresence(field) &&
          field.getContainingOneof() == null)
      ) {
        generator.writeFieldName(fieldName);
        writeValue(field, message.getField(field), generator, serializerProvider);
      } else if (include == Include.ALWAYS && field.getContainingOneof() == null) {
        generator.writeFieldName(fieldName);
        generator.writeNull();
      }
    }

    if (!isUnwrappingSerializer()) {
      generator.writeEndObject();
    }
  }

  @Override
  public boolean isUnwrappingSerializer() {
    return unwrappingSerializer;
  }

  @Override
  public MessageSerializer unwrappingSerializer(NameTransformer nameTransformer) {
    return new MessageSerializer(getConfig(), true);
  }

  @Override
  public void acceptJsonFormatVisitor(
    JsonFormatVisitorWrapper visitor,
    JavaType typeHint
  ) throws JsonMappingException {
    Message defaultInstance = defaultInstance(typeHint);
    Function<FieldDescriptor, String> propertyNaming = getPropertyNaming(
      defaultInstance,
      visitor.getProvider()
    );

    new MessageSchemaGenerator(defaultInstance, getConfig(), propertyNaming)
      .acceptJsonFormatVisitor(visitor, typeHint);
  }

  private static Message defaultInstance(JavaType typeHint) {
    Class<?> messageOrBuilderType = typeHint.getRawClass();

    final Class<?> messageType;
    if (Message.class.isAssignableFrom(messageOrBuilderType)) {
      messageType = messageOrBuilderType;
    } else if (Message.Builder.class.isAssignableFrom(messageOrBuilderType)) {
      messageType = messageOrBuilderType.getDeclaringClass();
    } else {
      throw new RuntimeException(
        "Class does not appear to extend Message or Message.Builder: " +
        messageOrBuilderType
      );
    }

    try {
      return (Message) messageType.getMethod("getDefaultInstance").invoke(null);
    } catch (Exception e) {
      throw new RuntimeException(
        "Unable to get default instance for type " + messageType,
        e
      );
    }
  }

  private Function<FieldDescriptor, String> getPropertyNaming(
    MessageOrBuilder messageOrBuilder,
    SerializerProvider serializerProvider
  ) {
    Descriptor descriptor = messageOrBuilder.getDescriptorForType();
    PropertyNamingCache cache = propertyNamingCache.get(descriptor);
    if (cache == null) {
      // use computeIfAbsent as a fallback since it allocates
      cache =
        propertyNamingCache.computeIfAbsent(
          descriptor,
          ignored ->
            PropertyNamingCache.forDescriptor(
              descriptor,
              messageOrBuilder.getDefaultInstanceForType().getClass(),
              getConfig()
            )
        );
    }

    return cache.forSerialization(serializerProvider.getConfig());
  }

  private static boolean supportsFieldPresence(FieldDescriptor field) {
    // messages still support field presence in proto3
    return (
      field.getJavaType() ==
      com.google.protobuf.Descriptors.FieldDescriptor.JavaType.MESSAGE
    );
  }

  private static boolean writeSingleElementArraysUnwrapped(SerializerProvider config) {
    return config.isEnabled(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED);
  }
}
