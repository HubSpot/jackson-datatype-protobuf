package com.hubspot.jackson.datatype.protobuf.builtin.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.google.protobuf.Timestamp;
import com.google.protobuf.util.Timestamps;
import com.hubspot.jackson.datatype.protobuf.ProtobufJacksonConfig;
import com.hubspot.jackson.datatype.protobuf.ProtobufSerializer;
import java.io.IOException;

public class TimestampSerializer extends ProtobufSerializer<Timestamp> {

  /**
   * @deprecated use {@link #TimestampSerializer(ProtobufJacksonConfig)}
   */
  @Deprecated
  public TimestampSerializer() {
    this(ProtobufJacksonConfig.getDefaultInstance());
  }

  public TimestampSerializer(ProtobufJacksonConfig config) {
    super(Timestamp.class, config);
  }

  @Override
  public void serialize(
    Timestamp timestamp,
    JsonGenerator generator,
    SerializerProvider serializerProvider
  ) throws IOException {
    generator.writeString(Timestamps.toString(timestamp));
  }

  @Override
  public void acceptJsonFormatVisitor(
    JsonFormatVisitorWrapper visitor,
    JavaType typeHint
  ) throws JsonMappingException {
    visitor.expectStringFormat(typeHint);
  }
}
