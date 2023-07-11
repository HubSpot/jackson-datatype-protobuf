package com.hubspot.jackson.datatype.protobuf.builtin.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.google.protobuf.FieldMask;
import com.google.protobuf.util.FieldMaskUtil;
import com.hubspot.jackson.datatype.protobuf.ProtobufJacksonConfig;
import com.hubspot.jackson.datatype.protobuf.ProtobufSerializer;
import java.io.IOException;

public class FieldMaskSerializer extends ProtobufSerializer<FieldMask> {

  @Deprecated
  public FieldMaskSerializer() {
    this(ProtobufJacksonConfig.getDefaultInstance());
  }

  public FieldMaskSerializer(ProtobufJacksonConfig config) {
    super(FieldMask.class, config);
  }

  @Override
  public void serialize(
          FieldMask fieldMask,
          JsonGenerator generator,
          SerializerProvider serializerProvider
  ) throws IOException {
    generator.writeString(FieldMaskUtil.toJsonString(fieldMask));
  }
}
