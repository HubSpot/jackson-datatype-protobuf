package com.hubspot.jackson.datatype.protobuf.builtin.deserializers;

import java.io.IOException;
import java.text.ParseException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.google.protobuf.Duration;
import com.google.protobuf.util.Durations;

public class DurationDeserializer extends StdDeserializer<Duration> {

  public DurationDeserializer() {
    super(Duration.class);
  }

  @Override
  public Duration deserialize(JsonParser parser, DeserializationContext context) throws IOException {
    switch (parser.getCurrentToken()) {
      case VALUE_STRING:
        try {
          return Durations.parse(parser.getText());
        } catch (ParseException e) {
          throw context.weirdStringException(parser.getText(), Duration.class, e.getMessage());
        }
      default:
        throw context.wrongTokenException(parser, JsonToken.VALUE_STRING, wrongTokenMessage(context));
    }
  }

  // TODO share this?
  private static String wrongTokenMessage(DeserializationContext context) {
    return "Can not deserialize instance of com.google.protobuf.Duration out of " + context.getParser().getCurrentToken() + " token";
  }
}
