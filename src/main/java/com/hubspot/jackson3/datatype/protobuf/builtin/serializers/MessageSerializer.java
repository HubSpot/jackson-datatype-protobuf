package com.hubspot.jackson3.datatype.protobuf.builtin.serializers;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.ExtensionRegistry.ExtensionInfo;
import com.google.protobuf.Message;
import com.google.protobuf.MessageOrBuilder;
import com.hubspot.jackson3.datatype.protobuf.ProtobufJacksonConfig;
import com.hubspot.jackson3.datatype.protobuf.ProtobufSerializer;
import com.hubspot.jackson3.datatype.protobuf.internal.MessageSchemaGenerator;
import com.hubspot.jackson3.datatype.protobuf.internal.PropertyNamingCache;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import tools.jackson.databind.util.NameTransformer;

public class MessageSerializer extends ProtobufSerializer<MessageOrBuilder> {

  private final boolean unwrappingSerializer;
  private final Map<Descriptor, PropertyNamingCache> propertyNamingCache;

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
    SerializationContext serializationContext
  ) {
    if (!isUnwrappingSerializer()) {
      generator.writeStartObject();
    }

    boolean proto3 =
      "proto3".equals(message.getDescriptorForType().getFile().toProto().getSyntax());
    Include include = serializationContext
      .getConfig()
      .getDefaultPropertyInclusion()
      .getValueInclusion();
    boolean writeDefaultValues = proto3 && include != Include.NON_DEFAULT;
    boolean writeEmptyCollections =
      include != Include.NON_DEFAULT && include != Include.NON_EMPTY;

    Function<FieldDescriptor, String> propertyNaming = getPropertyNaming(
      message,
      serializationContext
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
            generator.writeName(fieldName);
            writeMap(field, valueList, generator, serializationContext);
          } else if (
            valueList.size() == 1 &&
            writeSingleElementArraysUnwrapped(serializationContext)
          ) {
            generator.writeName(fieldName);
            writeValue(field, valueList.get(0), generator, serializationContext);
          } else {
            generator.writeArrayPropertyStart(fieldName);
            for (Object subValue : valueList) {
              writeValue(field, subValue, generator, serializationContext);
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
        generator.writeName(fieldName);
        writeValue(field, message.getField(field), generator, serializationContext);
      } else if (include == Include.ALWAYS && field.getContainingOneof() == null) {
        generator.writeName(fieldName);
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
  ) {
    Message defaultInstance = defaultInstance(typeHint);
    Function<FieldDescriptor, String> propertyNaming = getPropertyNaming(
      defaultInstance,
      visitor.getContext()
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
    SerializationContext serializationContext
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

    return cache.forSerialization(serializationContext.getConfig());
  }

  private static boolean supportsFieldPresence(FieldDescriptor field) {
    // messages still support field presence in proto3
    return (
      field.getJavaType() ==
      com.google.protobuf.Descriptors.FieldDescriptor.JavaType.MESSAGE
    );
  }

  private static boolean writeSingleElementArraysUnwrapped(
    SerializationContext serializationContext
  ) {
    return serializationContext.isEnabled(
      SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED
    );
  }
}
