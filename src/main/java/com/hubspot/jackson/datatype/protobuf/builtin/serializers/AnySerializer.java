package com.hubspot.jackson.datatype.protobuf.builtin.serializers;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.google.protobuf.Any;
import com.hubspot.jackson.datatype.protobuf.ProtobufSerializer;

public class AnySerializer extends ProtobufSerializer<Any> {

  public AnySerializer() {
    super(Any.class);
  }

  @Override
  public void serialize(
          Any any,
          JsonGenerator generator,
          SerializerProvider serializerProvider
  ) throws IOException {
    // TODO
  }
}
