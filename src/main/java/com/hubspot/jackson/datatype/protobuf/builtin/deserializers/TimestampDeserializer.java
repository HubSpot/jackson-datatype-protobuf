package com.hubspot.jackson.datatype.protobuf.builtin.deserializers;

import com.google.protobuf.Timestamp;
import com.google.protobuf.util.Timestamps;
import java.text.ParseException;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.deser.std.StdDeserializer;

public class TimestampDeserializer extends StdDeserializer<Timestamp> {

  public TimestampDeserializer() {
    super(Timestamp.class);
  }

  @Override
  public Timestamp deserialize(JsonParser parser, DeserializationContext context)
    throws JacksonException {
    switch (parser.currentToken()) {
      case VALUE_STRING:
        try {
          return Timestamps.parse(parser.getString());
        } catch (ParseException e) {
          throw context.weirdStringException(
            parser.getString(),
            Timestamp.class,
            e.getMessage()
          );
        }
      default:
        context.reportWrongTokenException(
          Timestamp.class,
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
      "Can not deserialize instance of com.google.protobuf.Timestamp out of " +
      context.getParser().currentToken() +
      " token"
    );
  }
}
