package com.hubspot.jackson.datatype.protobuf.builtin.serializers;

import com.hubspot.jackson.datatype.protobuf.ProtobufJacksonConfig;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.google.protobuf.FieldMask;
import com.google.protobuf.util.FieldMaskUtil;
import com.hubspot.jackson.datatype.protobuf.ProtobufSerializer;

public class FieldMaskSerializer extends ProtobufSerializer<FieldMask> {

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
