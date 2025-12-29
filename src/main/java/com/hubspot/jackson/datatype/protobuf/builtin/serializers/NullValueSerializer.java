package com.hubspot.jackson.datatype.protobuf.builtin.serializers;

import com.google.protobuf.NullValue;
import com.hubspot.jackson.datatype.protobuf.ProtobufJacksonConfig;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import tools.jackson.databind.ser.std.StdSerializer;

public class NullValueSerializer extends StdSerializer<NullValue> {

  public NullValueSerializer(ProtobufJacksonConfig config) {
    super(NullValue.class);
  }

  @Override
  public void serialize(
    NullValue value,
    JsonGenerator gen,
    SerializationContext serializationContext
  ) {
    gen.writeNull();
  }

  @Override
  public void acceptJsonFormatVisitor(
    JsonFormatVisitorWrapper visitor,
    JavaType typeHint
  ) {
    visitor.expectNullFormat(typeHint);
  }
}
