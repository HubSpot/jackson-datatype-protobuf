package com.hubspot.jackson.datatype.protobuf.builtin.deserializers;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.google.protobuf.Any;
import com.hubspot.jackson.datatype.protobuf.ProtobufDeserializer;

public class AnyDeserializer extends ProtobufDeserializer<Any, Any.Builder> {

  public AnyDeserializer() {
    super(Any.class);
  }

  @Override
  protected void populate(
          Any.Builder builder,
          JsonParser parser,
          DeserializationContext context
  ) throws IOException {
    // TODO
  }
}
