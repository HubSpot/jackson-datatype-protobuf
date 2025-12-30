package com.hubspot.jackson3.datatype.protobuf.builtin.serializers;

import com.google.protobuf.Duration;
import com.google.protobuf.util.Durations;
import com.hubspot.jackson3.datatype.protobuf.ProtobufJacksonConfig;
import com.hubspot.jackson3.datatype.protobuf.ProtobufSerializer;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;

public class DurationSerializer extends ProtobufSerializer<Duration> {

  public DurationSerializer(ProtobufJacksonConfig config) {
    super(Duration.class, config);
  }

  @Override
  public void serialize(
    Duration duration,
    JsonGenerator generator,
    SerializationContext serializationContext
  ) {
    generator.writeString(Durations.toString(duration));
  }

  @Override
  public void acceptJsonFormatVisitor(
    JsonFormatVisitorWrapper visitor,
    JavaType typeHint
  ) {
    visitor.expectStringFormat(typeHint);
  }
}
