package com.hubspot.jackson.datatype.protobuf.internal;

import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.NullValue;
import com.hubspot.jackson.datatype.protobuf.ProtobufJacksonConfig;
import java.math.BigInteger;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.cfg.EnumFeature;
import tools.jackson.databind.jsonFormatVisitors.JsonFormatVisitable;
import tools.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import tools.jackson.databind.ser.std.StdSerializer;

public class FieldSchemaGenerator
  extends StdSerializer<String>
  implements JsonFormatVisitable {

  private final FieldDescriptor field;
  private final ProtobufJacksonConfig config;

  public FieldSchemaGenerator(FieldDescriptor field, ProtobufJacksonConfig config) {
    super(String.class);
    this.field = field;
    this.config = config;
  }

  @Override
  public void serialize(
    String value,
    JsonGenerator gen,
    SerializationContext serializationContext
  ) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void acceptJsonFormatVisitor(
    JsonFormatVisitorWrapper visitor,
    JavaType typeHint
  ) {
    switch (field.getJavaType()) {
      case INT:
        if (
          Types.isUnsigned(field.getType()) && config.properUnsignedNumberSerialization()
        ) {
          visitIntFormat(
            visitor,
            visitor.getContext().constructType(Long.class),
            JsonParser.NumberType.LONG
          );
        } else {
          visitIntFormat(visitor, typeHint, JsonParser.NumberType.INT);
        }
        break;
      case LONG:
        if (config.serializeLongsAsString()) {
          visitor.expectStringFormat(visitor.getContext().constructType(String.class));
        } else if (
          Types.isUnsigned(field.getType()) && config.properUnsignedNumberSerialization()
        ) {
          visitIntFormat(
            visitor,
            visitor.getContext().constructType(BigInteger.class),
            JsonParser.NumberType.BIG_INTEGER
          );
        } else {
          visitIntFormat(visitor, typeHint, JsonParser.NumberType.LONG);
        }
        break;
      case FLOAT:
        visitFloatFormat(visitor, typeHint, JsonParser.NumberType.FLOAT);
        break;
      case DOUBLE:
        visitFloatFormat(visitor, typeHint, JsonParser.NumberType.DOUBLE);
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
        } else if (visitor.getContext().isEnabled(EnumFeature.WRITE_ENUMS_USING_INDEX)) {
          visitor.expectIntegerFormat(typeHint);
        } else {
          visitor.expectStringFormat(typeHint);
        }
        break;
      case MESSAGE:
        ValueSerializer<Object> serializer = visitor
          .getContext()
          .findValueSerializer(typeHint.getRawClass());
        serializer.acceptJsonFormatVisitor(visitor, typeHint);
        break;
    }
  }
}
