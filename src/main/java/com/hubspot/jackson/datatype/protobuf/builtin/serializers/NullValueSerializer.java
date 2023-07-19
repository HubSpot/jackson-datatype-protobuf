package com.hubspot.jackson.datatype.protobuf.builtin.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.google.protobuf.NullValue;
import com.hubspot.jackson.datatype.protobuf.ProtobufJacksonConfig;
import java.io.IOException;

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
  public void serialize(NullValue value, JsonGenerator gen, SerializerProvider provider)
    throws IOException {
    gen.writeNull();
  }

  @Override
  public void acceptJsonFormatVisitor(
    JsonFormatVisitorWrapper visitor,
    JavaType typeHint
  ) throws JsonMappingException {
    visitor.expectNullFormat(typeHint);
  }
}
