package com.hubspot.jackson3.datatype.protobuf.builtin.deserializers;

import com.google.protobuf.NullValue;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.deser.std.StdDeserializer;

public class NullValueDeserializer extends StdDeserializer<NullValue> {

  public NullValueDeserializer() {
    super(NullValue.class);
  }

  @Override
  public NullValue deserialize(JsonParser parser, DeserializationContext context) {
    switch (parser.currentToken()) {
      case VALUE_NULL:
        return NullValue.NULL_VALUE;
      default:
        context.reportWrongTokenException(
          NullValue.class,
          JsonToken.VALUE_NULL,
          wrongTokenMessage(context)
        );
        // the previous method should have thrown
        throw new AssertionError();
    }
  }

  @Override
  public NullValue getNullValue(DeserializationContext ctxt) {
    return NullValue.NULL_VALUE;
  }

  // TODO share this?
  private static String wrongTokenMessage(DeserializationContext context) {
    return (
      "Can not deserialize instance of com.google.protobuf.NullValue out of " +
      context.getParser().currentToken() +
      " token"
    );
  }
}
