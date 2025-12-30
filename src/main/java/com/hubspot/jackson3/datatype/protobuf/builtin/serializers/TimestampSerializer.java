package com.hubspot.jackson3.datatype.protobuf.builtin.serializers;

import com.google.protobuf.Timestamp;
import com.google.protobuf.util.Timestamps;
import com.hubspot.jackson3.datatype.protobuf.ProtobufJacksonConfig;
import com.hubspot.jackson3.datatype.protobuf.ProtobufSerializer;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;

public class TimestampSerializer extends ProtobufSerializer<Timestamp> {

  public TimestampSerializer(ProtobufJacksonConfig config) {
    super(Timestamp.class, config);
  }

  @Override
  public void serialize(
    Timestamp timestamp,
    JsonGenerator generator,
    SerializationContext serializationContext
  ) {
    generator.writeString(Timestamps.toString(timestamp));
  }

  @Override
  public void acceptJsonFormatVisitor(
    JsonFormatVisitorWrapper visitor,
    JavaType typeHint
  ) {
    visitor.expectStringFormat(typeHint);
  }
}
