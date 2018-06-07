package com.hubspot.jackson.datatype.protobuf.builtin.serializers;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser.NumberType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.PropertyNamingStrategyBase;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitable;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor.JavaType;
import com.google.protobuf.Descriptors.FileDescriptor.Syntax;
import com.google.protobuf.ExtensionRegistry.ExtensionInfo;
import com.google.protobuf.GeneratedMessageV3.ExtendableMessageOrBuilder;
import com.google.protobuf.Message;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.NullValue;
import com.hubspot.jackson.datatype.protobuf.ExtensionRegistryWrapper;
import com.hubspot.jackson.datatype.protobuf.PropertyNamingStrategyWrapper;
import com.hubspot.jackson.datatype.protobuf.ProtobufJacksonConfig;
import com.hubspot.jackson.datatype.protobuf.ProtobufSerializer;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public class MessageSerializer extends ProtobufSerializer<MessageOrBuilder> {
  @SuppressFBWarnings(value="SE_BAD_FIELD")
  private final ProtobufJacksonConfig config;

  /**
   * @deprecated use {@link #MessageSerializer(ProtobufJacksonConfig)} instead
   */
  @Deprecated
  public MessageSerializer(ExtensionRegistryWrapper extensionRegistry) {
    this(ProtobufJacksonConfig.builder().extensionRegistry(extensionRegistry).build());
  }

  public MessageSerializer(ProtobufJacksonConfig config) {
    super(MessageOrBuilder.class);

    this.config = config;
  }

  @Override
  public void serialize(
          MessageOrBuilder message,
          JsonGenerator generator,
          SerializerProvider serializerProvider
  ) throws IOException {
    generator.writeStartObject();

    boolean proto3 = message.getDescriptorForType().getFile().getSyntax() == Syntax.PROTO3;
    Include include = serializerProvider.getConfig().getDefaultPropertyInclusion().getValueInclusion();
    boolean writeDefaultValues = proto3 && include != Include.NON_DEFAULT;
    boolean writeEmptyCollections = include != Include.NON_DEFAULT && include != Include.NON_EMPTY;
    PropertyNamingStrategyBase namingStrategy =
            new PropertyNamingStrategyWrapper(serializerProvider.getConfig().getPropertyNamingStrategy());

    Descriptor descriptor = message.getDescriptorForType();
    List<FieldDescriptor> fields = new ArrayList<>(descriptor.getFields());
    if (message instanceof ExtendableMessageOrBuilder<?>) {
      for (ExtensionInfo extensionInfo : config.extensionRegistry().getExtensionsByDescriptor(descriptor)) {
        fields.add(extensionInfo.descriptor);
      }
    }

    for (FieldDescriptor field : fields) {
      if (field.isRepeated()) {
        List<?> valueList = (List<?>) message.getField(field);

        if (!valueList.isEmpty() || writeEmptyCollections) {
          if (field.isMapField()) {
            generator.writeFieldName(namingStrategy.translate(field.getName()));
            writeMap(field, valueList, generator, serializerProvider);
          } else if (valueList.size() == 1 && writeSingleElementArraysUnwrapped(serializerProvider)) {
            generator.writeFieldName(namingStrategy.translate(field.getName()));
            writeValue(field, valueList.get(0), generator, serializerProvider);
          } else {
            generator.writeArrayFieldStart(namingStrategy.translate(field.getName()));
            for (Object subValue : valueList) {
              writeValue(field, subValue, generator, serializerProvider);
            }
            generator.writeEndArray();
          }
        }
      } else if (message.hasField(field) || (writeDefaultValues && !supportsFieldPresence(field) && field.getContainingOneof() == null)) {
        generator.writeFieldName(namingStrategy.translate(field.getName()));
        writeValue(field, message.getField(field), generator, serializerProvider);
      } else if (include == Include.ALWAYS && field.getContainingOneof() == null) {
        generator.writeFieldName(namingStrategy.translate(field.getName()));
        generator.writeNull();
      }
    }

    generator.writeEndObject();
  }

  @Override
  public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, com.fasterxml.jackson.databind.JavaType typeHint) throws JsonMappingException {
    // TODO
    Class<?> messageOrBuilderType = typeHint.getRawClass();

    final Class<?> messageType;
    if (Message.class.isAssignableFrom(messageOrBuilderType)) {
      messageType = messageOrBuilderType;
    } else if (Message.Builder.class.isAssignableFrom(messageOrBuilderType)) {
      messageType = messageOrBuilderType.getDeclaringClass();
    } else {
      throw new RuntimeException("Class does not appear to extend Message or Message.Builder: " + messageOrBuilderType);
    }


    JsonObjectFormatVisitor v2 = visitor.expectObjectFormat(typeHint);

    final Message defaultInstance = defaultInstance(messageType);
    Descriptor descriptor = defaultInstance.getDescriptorForType();
    PropertyNamingStrategyBase namingStrategy =
        new PropertyNamingStrategyWrapper(visitor.getProvider().getConfig().getPropertyNamingStrategy());

    List<FieldDescriptor> fields = new ArrayList<>(descriptor.getFields());
    if (defaultInstance instanceof ExtendableMessageOrBuilder<?>) {
      for (ExtensionInfo extensionInfo : config.extensionRegistry().getExtensionsByDescriptor(descriptor)) {
        fields.add(extensionInfo.descriptor);
      }
    }

    for (final FieldDescriptor field : fields) {
      String fieldName = namingStrategy.translate(field.getName());
      final com.fasterxml.jackson.databind.JavaType fieldType =
          visitor.getProvider().constructType(fieldClass(defaultInstance, field));
      if (field.isMapField()) {
        // TODO
      } else if (field.isRepeated()) {
        // TODO
      } else {
        v2.optionalProperty(fieldName, new JsonFormatVisitable() {

          @Override
          public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, com.fasterxml.jackson.databind.JavaType typeHint) throws JsonMappingException {
            switch (field.getJavaType()) {
              case INT:
                visitIntFormat(visitor, fieldType, NumberType.INT);
                break;
              case LONG:
                visitIntFormat(visitor, fieldType, NumberType.LONG);
                break;
              case FLOAT:
                visitFloatFormat(visitor, fieldType, NumberType.FLOAT);
                break;
              case DOUBLE:
                visitFloatFormat(visitor, fieldType, NumberType.DOUBLE);
                break;
              case BOOLEAN:
                visitor.expectBooleanFormat(fieldType);
                break;
              case STRING:
              case BYTE_STRING:
                visitor.expectStringFormat(fieldType);
                break;
              case ENUM:
                if (fieldType.getRawClass() == NullValue.class) {
                  visitor.expectNullFormat(fieldType);
                } else if (writeEnumsUsingIndex(visitor.getProvider())) {
                  visitor.expectIntegerFormat(fieldType);
                } else {
                  visitor.expectStringFormat(fieldType);
                }
                break;
              case MESSAGE:
                JsonSerializer<Object> serializer =
                    visitor.getProvider().findValueSerializer(fieldType.getRawClass(), null);
                serializer.acceptJsonFormatVisitor(visitor, fieldType);
                break;
            }
          }
        }, fieldType);
      }
    }
  }

  @Override
  public JsonNode getSchema(SerializerProvider provider, Type typeHint) throws JsonMappingException {
    // TODO
    return createSchemaNode("object", true);
  }

  private static Message defaultInstance(Class<?> messageType) {
    try {
      return (Message) messageType.getMethod("getDefaultInstance").invoke(null);
    } catch (Exception e) {
      throw new RuntimeException("Unable to get default instance for type " + messageType, e);
    }
  }

  private static Class<?> fieldClass(Message defaultInstance, FieldDescriptor field) {
    // return boxed types otherwise we may accidentally say everything is required
    switch (field.getJavaType()) {
      case INT:
        return Integer.class;
      case LONG:
        return Long.class;
      case FLOAT:
        return Float.class;
      case DOUBLE:
        return Double.class;
      case BOOLEAN:
        return Boolean.class;
      case STRING:
      // just return String for ByteString, otherwise we may accidentally introspect the ByteString proto
      case BYTE_STRING:
        return String.class;
      case ENUM:
        // Hacky but I don't see an easier way to find exact enum class
        String getterName = "get" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);

        try {
          Method getterMethod = defaultInstance.getClass().getMethod(getterName);
          return getterMethod.invoke(defaultInstance).getClass();
        } catch (ReflectiveOperationException e) {
          throw new RuntimeException(e);
        }
      case MESSAGE:
        Message.Builder subBuilder = defaultInstance.toBuilder().newBuilderForField(field);
        return subBuilder.getDefaultInstanceForType().getClass();
      default:
        throw new IllegalArgumentException("Unknown field type: " + field.getJavaType());
    }
  }

  private static boolean supportsFieldPresence(FieldDescriptor field) {
    // messages still support field presence in proto3
    return field.getJavaType() == JavaType.MESSAGE;
  }

  private static boolean writeEmptyArrays(SerializerProvider config) {
    return config.isEnabled(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS);
  }

  private static boolean writeSingleElementArraysUnwrapped(SerializerProvider config) {
    return config.isEnabled(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED);
  }
}
