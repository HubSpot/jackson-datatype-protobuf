package com.hubspot.jackson.datatype.protobuf.builtin.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.google.protobuf.Duration;
import com.google.protobuf.util.Durations;
import com.hubspot.jackson.datatype.protobuf.ProtobufJacksonConfig;
import com.hubspot.jackson.datatype.protobuf.ProtobufSerializer;
import java.io.IOException;

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
          SerializerProvider serializerProvider
  ) throws IOException {
    generator.writeString(Durations.toString(duration));
  }
}
