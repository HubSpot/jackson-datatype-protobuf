package com.hubspot.jackson.datatype.protobuf.builtin.serializers;

import com.google.protobuf.Duration;
import com.google.protobuf.util.Durations;
import com.hubspot.jackson.datatype.protobuf.ProtobufJacksonConfig;
import com.hubspot.jackson.datatype.protobuf.ProtobufSerializer;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;

public class DurationSerializer extends ProtobufSerializer<Duration> {

  /**
   * @deprecated use {@link #DurationSerializer(ProtobufJacksonConfig)}
   */
  @Deprecated
  public DurationSerializer() {
    this(ProtobufJacksonConfig.getDefaultInstance());
  }

  public DurationSerializer(ProtobufJacksonConfig config) {
    super(Duration.class, config);
  }

  @Override
  public void serialize(
    Duration duration,
    JsonGenerator generator,
    SerializationContext serializerProvider
  ) throws JacksonException {
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
