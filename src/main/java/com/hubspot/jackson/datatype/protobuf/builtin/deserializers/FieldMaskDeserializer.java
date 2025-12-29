package com.hubspot.jackson.datatype.protobuf.builtin.deserializers;

import com.google.protobuf.FieldMask;
import com.google.protobuf.util.FieldMaskUtil;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.deser.std.StdDeserializer;

public class FieldMaskDeserializer extends StdDeserializer<FieldMask> {

  public FieldMaskDeserializer() {
    super(FieldMask.class);
  }

  @Override
  public FieldMask deserialize(JsonParser parser, DeserializationContext context) {
    switch (parser.currentToken()) {
      case VALUE_STRING:
        return FieldMaskUtil.fromJsonString(parser.getString());
      default:
        context.reportWrongTokenException(
          FieldMask.class,
          JsonToken.VALUE_STRING,
          wrongTokenMessage(context)
        );
        // the previous method should have thrown
        throw new AssertionError();
    }
  }

  // TODO share this?
  private static String wrongTokenMessage(DeserializationContext context) {
    return (
      "Can not deserialize instance of com.google.protobuf.FieldMask out of " +
      context.getParser().currentToken() +
      " token"
    );
  }
}
