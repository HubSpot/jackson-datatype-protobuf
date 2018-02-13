package com.hubspot.jackson.datatype.protobuf.builtin.deserializers;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.google.protobuf.NullValue;

public class NullValueDeserializer extends StdDeserializer<NullValue> {

  public NullValueDeserializer() {
    super(NullValue.class);
  }

  @Override
  public NullValue deserialize(JsonParser parser, DeserializationContext context) throws IOException {
    switch (parser.getCurrentToken()) {
      case VALUE_NULL:
        return NullValue.NULL_VALUE;
      default:
        throw context.wrongTokenException(parser, JsonToken.VALUE_NULL, wrongTokenMessage(context));
    }
  }

  @Override
  public NullValue getNullValue(DeserializationContext ctxt) {
    return NullValue.NULL_VALUE;
  }

  // TODO share this?
  private static String wrongTokenMessage(DeserializationContext context) {
    return "Can not deserialize instance of com.google.protobuf.NullValue out of " + context.getParser().getCurrentToken() + " token";
  }
}
