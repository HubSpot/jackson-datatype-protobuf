package com.hubspot.jackson.datatype.protobuf.builtin.serializers;

import com.google.protobuf.NullValue;
import com.hubspot.jackson.datatype.protobuf.ProtobufJacksonConfig;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import tools.jackson.databind.ser.std.StdSerializer;

public class NullValueSerializer extends StdSerializer<NullValue> {

  /**
   * @deprecated use {@link #NullValueSerializer(ProtobufJacksonConfig)}
   */
  @Deprecated
  public NullValueSerializer() {
    this(ProtobufJacksonConfig.getDefaultInstance());
  }

  public NullValueSerializer(ProtobufJacksonConfig config) {
    super(NullValue.class);
  }

  @Override
  public void serialize(
    NullValue value,
    JsonGenerator gen,
    SerializationContext provider
  ) throws JacksonException {
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
