package com.hubspot.jackson.datatype.protobuf.builtin.serializers;

import java.io.IOException;
import java.lang.reflect.Type;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser.NumberType;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.MessageOrBuilder;
import com.hubspot.jackson.datatype.protobuf.ProtobufSerializer;

public class WrappedPrimitiveSerializer<T extends MessageOrBuilder> extends ProtobufSerializer<T> {

  public WrappedPrimitiveSerializer(Class<T> wrapperType) {
    super(wrapperType);
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
      case "UInt64Value":
        visitIntFormat(visitor, visitor.getProvider().constructType(Long.class), NumberType.LONG);
        break;
      case "Int32Value":
      case "UInt32Value":
        visitIntFormat(visitor, visitor.getProvider().constructType(Integer.class), NumberType.INT);
        break;
      default:
        throw new IllegalStateException("Unexpected wrapper type: " + handledType());
    }
  }

  @Override
  public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
    switch (handledType().getSimpleName()) {
      case "StringValue":
      case "BytesValue":
        return createSchemaNode("string", true);
      case "BoolValue":
        return createSchemaNode("boolean", true);
      case "DoubleValue":
      case "FloatValue":
      case "Int64Value":
      case "UInt64Value":
        return createSchemaNode("number", true);
      case "Int32Value":
      case "UInt32Value":
        return createSchemaNode("integer", true);
      default:
        throw new IllegalStateException("Unexpected wrapper type: " + handledType());
    }
  }
}
