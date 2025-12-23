package com.hubspot.jackson.datatype.protobuf.builtin.deserializers;

import com.google.protobuf.Duration;
import com.google.protobuf.util.Durations;
import java.text.ParseException;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.deser.std.StdDeserializer;

public class DurationDeserializer extends StdDeserializer<Duration> {

  public DurationDeserializer() {
    super(Duration.class);
  }

  @Override
  public Duration deserialize(JsonParser parser, DeserializationContext context)
    throws JacksonException {
    switch (parser.currentToken()) {
      case VALUE_STRING:
        try {
          return Durations.parse(parser.getString());
        } catch (ParseException e) {
          throw context.weirdStringException(
            parser.getString(),
            Duration.class,
            e.getMessage()
          );
        }
      default:
        context.reportWrongTokenException(
          Duration.class,
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
      "Can not deserialize instance of com.google.protobuf.Duration out of " +
      context.getParser().currentToken() +
      " token"
    );
  }
}
