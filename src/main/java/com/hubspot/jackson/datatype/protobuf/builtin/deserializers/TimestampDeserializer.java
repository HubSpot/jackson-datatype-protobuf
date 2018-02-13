package com.hubspot.jackson.datatype.protobuf.builtin.deserializers;

import java.io.IOException;
import java.text.ParseException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.google.protobuf.Timestamp;
import com.google.protobuf.util.Timestamps;

public class TimestampDeserializer extends StdDeserializer<Timestamp> {

  public TimestampDeserializer() {
    super(Timestamp.class);
  }

  @Override
  public Timestamp deserialize(JsonParser parser, DeserializationContext context) throws IOException {
    switch (parser.getCurrentToken()) {
      case VALUE_STRING:
        try {
          return Timestamps.parse(parser.getText());
        } catch (ParseException e) {
          throw context.weirdStringException(parser.getText(), Timestamp.class, e.getMessage());
        }
      default:
        context.reportWrongTokenException(parser, JsonToken.VALUE_STRING, wrongTokenMessage(context));
        // the previous method should have thrown
        throw new AssertionError();
    }
  }

  // TODO share this?
  private static String wrongTokenMessage(DeserializationContext context) {
    return "Can not deserialize instance of com.google.protobuf.Timestamp out of " + context.getParser().getCurrentToken() + " token";
  }
}
