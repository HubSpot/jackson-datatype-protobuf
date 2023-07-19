package com.hubspot.jackson.datatype.protobuf.internal;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser.NumberType;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonArrayFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitable;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonMapFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.databind.type.ArrayType;
import com.google.common.base.CaseFormat;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.ExtensionRegistry.ExtensionInfo;
import com.google.protobuf.GeneratedMessageV3.ExtendableMessageOrBuilder;
import com.google.protobuf.Message;
import com.google.protobuf.NullValue;
import com.hubspot.jackson.datatype.protobuf.ProtobufJacksonConfig;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class MessageSchemaGenerator implements JsonFormatVisitable {
  private final Message defaultInstance;
  private final Descriptor descriptor;
  private final ProtobufJacksonConfig config;
  private final Function<FieldDescriptor, String> propertyNaming;

  public MessageSchemaGenerator(Message defaultInstance, Descriptor descriptor, ProtobufJacksonConfig config, Function<FieldDescriptor, String> propertyNaming) {
    this.defaultInstance = defaultInstance;
    this.descriptor = descriptor;
    this.config = config;
    this.propertyNaming = propertyNaming;
  }

  @Override
  public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
    JsonObjectFormatVisitor objectVisitor = visitor.expectObjectFormat(typeHint);

    List<FieldDescriptor> fields = new ArrayList<>(descriptor.getFields());
    if (defaultInstance instanceof ExtendableMessageOrBuilder<?>) {
      for (ExtensionInfo extensionInfo : config.extensionRegistry().getExtensionsByDescriptor(descriptor)) {
        fields.add(extensionInfo.descriptor);
      }
    }

    for (final FieldDescriptor field : fields) {
      String fieldName = propertyNaming.apply(field);
      JavaType fieldType =
          visitor.getProvider().constructType(fieldClass(defaultInstance, field));
      JsonFormatVisitable fieldVisitable = new MessageFieldVisitable(field);

      if (field.isMapField()) {
        Message defaultMapEntry = defaultInstance.toBuilder().newBuilderForField(field).getDefaultInstanceForType();
        Descriptor entryDescriptor = defaultMapEntry.getDescriptorForType();
        FieldDescriptor keyDescriptor = entryDescriptor.findFieldByName("key");
        FieldDescriptor valueDescriptor = entryDescriptor.findFieldByName("value");

        // Hacky but I don't see an easier way to find exact map key/value class
        final JavaType mapType;
        try {
           mapType = visitor.getProvider().constructType(defaultInstance.getClass().getMethod(getterName(field)).getGenericReturnType());
        } catch (ReflectiveOperationException e) {
          throw new RuntimeException(e);
        }

        objectVisitor.optionalProperty(fieldName, (fieldVisitor, ignored) -> {
            JsonMapFormatVisitor mapVisitor = fieldVisitor.expectMapFormat(mapType);
            if (mapVisitor != null) {
              mapVisitor.keyFormat(new MessageFieldVisitable(keyDescriptor), mapType.getKeyType());
              mapVisitor.valueFormat(new MessageFieldVisitable(valueDescriptor), mapType.getContentType());
            }
        }, mapType);
      } else if (field.isRepeated()) {
        ArrayType listType = visitor.getProvider().getTypeFactory().constructArrayType(fieldType);

        objectVisitor.optionalProperty(fieldName, (fieldVisitor, ignored) -> {
          JsonArrayFormatVisitor arrayVisitor = fieldVisitor.expectArrayFormat(listType);
          if (arrayVisitor != null) {
            arrayVisitor.itemsFormat(fieldVisitable, fieldType);
          }
        }, listType);
      } else {
        objectVisitor.optionalProperty(fieldName, fieldVisitable, fieldType);
      }
    }
  }

  private Class<?> fieldClass(Message defaultInstance, FieldDescriptor field) {
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
        try {
          final Method getterMethod;
          if (field.isRepeated()) {
            // for repeated fields it takes an int index argument
            getterMethod = defaultInstance.getClass().getMethod(getterName(field), Integer.TYPE);
          } else {
            getterMethod = defaultInstance.getClass().getMethod(getterName(field));
          }

          return getterMethod.getReturnType();
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

  private static String getterName(FieldDescriptor field) {
    return "get" + CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, field.getName());
  }

  private class MessageFieldVisitable extends StdSerializer<String> implements JsonFormatVisitable {
    private final FieldDescriptor field;

    public MessageFieldVisitable(FieldDescriptor field) {
      super(String.class);
      this.field = field;
    }

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider provider) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void acceptJsonFormatVisitor(
        JsonFormatVisitorWrapper visitor,
        JavaType typeHint
    ) throws JsonMappingException {
      switch (field.getJavaType()) {
        case INT:
          if (Types.isUnsigned(field.getType()) && config.properUnsignedNumberSerialization()) {
            visitIntFormat(visitor, visitor.getProvider().constructType(Long.class), NumberType.LONG);
          } else {
            visitIntFormat(visitor, typeHint, NumberType.INT);
          }
          break;
        case LONG:
          if (config.serializeLongsAsString()) {
            visitor.expectStringFormat(visitor.getProvider().constructType(String.class));
          } else if (Types.isUnsigned(field.getType()) && config.properUnsignedNumberSerialization()) {
            visitIntFormat(visitor, visitor.getProvider().constructType(BigInteger.class), NumberType.BIG_INTEGER);
          } else {
            visitIntFormat(visitor, typeHint, NumberType.LONG);
          }
          break;
        case FLOAT:
          visitFloatFormat(visitor, typeHint, NumberType.FLOAT);
          break;
        case DOUBLE:
          visitFloatFormat(visitor, typeHint, NumberType.DOUBLE);
          break;
        case BOOLEAN:
          visitor.expectBooleanFormat(typeHint);
          break;
        case STRING:
        case BYTE_STRING:
          visitor.expectStringFormat(typeHint);
          break;
        case ENUM:
          if (typeHint.getRawClass() == NullValue.class) {
            visitor.expectNullFormat(typeHint);
          } else if (visitor.getProvider().isEnabled(SerializationFeature.WRITE_ENUMS_USING_INDEX)) {
            visitor.expectIntegerFormat(typeHint);
          } else {
            visitor.expectStringFormat(typeHint);
          }
          break;
        case MESSAGE:
          JsonSerializer<Object> serializer =
              visitor.getProvider().findValueSerializer(typeHint.getRawClass(), null);;
          serializer.acceptJsonFormatVisitor(visitor, typeHint);
          break;
      }
    }
  }
}
