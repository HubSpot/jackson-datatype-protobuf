package com.hubspot.jackson.datatype.protobuf.builtin.deserializers;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.exc.InputCoercionException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.NullValue;
import com.google.protobuf.Value;
import com.hubspot.jackson.datatype.protobuf.ProtobufDeserializer;

public class ValueDeserializer extends ProtobufDeserializer<Value, Value.Builder> {
  private static final FieldDescriptor STRUCT_FIELD = Value.getDescriptor().findFieldByName("struct_value");
  private static final FieldDescriptor LIST_FIELD = Value.getDescriptor().findFieldByName("list_value");

  public ValueDeserializer() {
    super(Value.class);
  }

  @Override
  protected void populate(
          Value.Builder builder,
          JsonParser parser,
          DeserializationContext context
  ) throws IOException {
    switch (parser.getCurrentToken()) {
      case START_OBJECT:
        Object structValue = readValue(builder, STRUCT_FIELD, null, parser, context);
        builder.setField(STRUCT_FIELD, structValue);
        return;
      case START_ARRAY:
        Object listValue = readValue(builder, LIST_FIELD, null, parser, context);
        builder.setField(LIST_FIELD, listValue);
        return;
      case VALUE_STRING:
        builder.setStringValue(parser.getText());
        return;
      case VALUE_NUMBER_INT:
        long longValue = parser.getLongValue();
        builder.setNumberValue(safeCast(longValue, context));
        return;
      case VALUE_NUMBER_FLOAT:
        builder.setNumberValue(parser.getDoubleValue());
        return;
      case VALUE_TRUE:
        builder.setBoolValue(true);
        return;
      case VALUE_FALSE:
        builder.setBoolValue(false);
        return;
      case VALUE_NULL:
        builder.setNullValue(NullValue.NULL_VALUE);
        return;
      default:
        String message = "Can not deserialize instance of com.google.protobuf.Value out of " + parser.currentToken() + " token";
        context.reportInputMismatch(Value.class, message);
        // the previous method should have thrown
        throw new AssertionError();
    }
  }

  @Override
  public Value.Builder getNullValue(DeserializationContext ctxt) {
    return Value.newBuilder().setNullValue(NullValue.NULL_VALUE);
  }

  double safeCast(long longValue, DeserializationContext context) throws JsonProcessingException {
    double doubleValue = (double) longValue;

    if (Double.valueOf(doubleValue).longValue() == longValue) {
      return doubleValue;
    } else {
      String message = String.format(
          "Number %d can not be represented as a double without loss of precision",
          longValue
      );
      throw new InputCoercionException(
          context.getParser(),
          message,
          JsonToken.VALUE_NUMBER_INT,
          Double.TYPE
      );
    }
  }
}
