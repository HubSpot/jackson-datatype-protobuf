package com.hubspot.jackson.datatype.protobuf.builtin.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser.NumberType;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.MessageOrBuilder;
import com.hubspot.jackson.datatype.protobuf.ProtobufJacksonConfig;
import com.hubspot.jackson.datatype.protobuf.ProtobufSerializer;
import java.io.IOException;
import java.math.BigInteger;

public class WrappedPrimitiveSerializer<T extends MessageOrBuilder> extends ProtobufSerializer<T> {

  /**
   * @deprecated use {@link #WrappedPrimitiveSerializer(Class, ProtobufJacksonConfig)}
   */
  @Deprecated
  public WrappedPrimitiveSerializer(Class<T> wrapperType) {
    this(wrapperType, ProtobufJacksonConfig.getDefaultInstance());
  }

  public WrappedPrimitiveSerializer(Class<T> wrapperType, ProtobufJacksonConfig config) {
    super(wrapperType, config);
  }

  @Override
  public void serialize(
          MessageOrBuilder message,
          JsonGenerator generator,
          SerializerProvider serializerProvider
  ) throws IOException {
    FieldDescriptor field = message.getDescriptorForType().findFieldByName("value");
    Object value = message.getField(field);
    writeValue(field, value, generator, serializerProvider);
  }

  @Override
  public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
    switch (handledType().getSimpleName()) {
      case "StringValue":
      case "BytesValue":
        visitor.expectStringFormat(visitor.getProvider().constructType(String.class));
        break;
      case "BoolValue":
        visitor.expectBooleanFormat(visitor.getProvider().constructType(Boolean.class));
        break;
      case "DoubleValue":
        visitFloatFormat(visitor, visitor.getProvider().constructType(Double.class), NumberType.DOUBLE);
        break;
      case "FloatValue":
        visitFloatFormat(visitor, visitor.getProvider().constructType(Float.class), NumberType.FLOAT);
        break;
      case "Int64Value":
        if (getConfig().serializeLongsAsString()) {
          visitor.expectStringFormat(visitor.getProvider().constructType(String.class));
        } else {
          visitIntFormat(visitor, visitor.getProvider().constructType(Long.class), NumberType.LONG);
        }
        break;
      case "UInt64Value":
        if (getConfig().serializeLongsAsString()) {
          visitor.expectStringFormat(visitor.getProvider().constructType(String.class));
        } else if (getConfig().properUnsignedNumberSerialization()) {
          visitIntFormat(visitor, visitor.getProvider().constructType(BigInteger.class), NumberType.BIG_INTEGER);
        } else {
          visitIntFormat(visitor, visitor.getProvider().constructType(Long.class), NumberType.LONG);
        }
        break;
      case "Int32Value":
        visitIntFormat(visitor, visitor.getProvider().constructType(Integer.class), NumberType.INT);
        break;
      case "UInt32Value":
        if (getConfig().properUnsignedNumberSerialization()) {
          visitIntFormat(visitor, visitor.getProvider().constructType(Long.class), NumberType.LONG);
        } else {
          visitIntFormat(visitor, visitor.getProvider().constructType(Integer.class), NumberType.INT);
        }
        break;
      default:
        throw new IllegalStateException("Unexpected wrapper type: " + handledType());
    }
  }
}
