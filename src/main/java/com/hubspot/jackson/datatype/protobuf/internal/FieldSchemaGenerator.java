package com.hubspot.jackson.datatype.protobuf.internal;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser.NumberType;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitable;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.NullValue;
import com.hubspot.jackson.datatype.protobuf.ProtobufJacksonConfig;
import java.math.BigInteger;

public class FieldSchemaGenerator extends StdSerializer<String> implements JsonFormatVisitable {
  private final FieldDescriptor field;
  private final ProtobufJacksonConfig config;

  public FieldSchemaGenerator(FieldDescriptor field, ProtobufJacksonConfig config) {
    super(String.class);
    this.field = field;
    this.config = config;
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
