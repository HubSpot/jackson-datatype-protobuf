package com.hubspot.jackson.datatype.protobuf.builtin.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.google.protobuf.FieldMask;
import com.google.protobuf.util.FieldMaskUtil;
import com.hubspot.jackson.datatype.protobuf.ProtobufJacksonConfig;
import com.hubspot.jackson.datatype.protobuf.ProtobufSerializer;
import java.io.IOException;

public class FieldMaskSerializer extends ProtobufSerializer<FieldMask> {

  /**
   * @deprecated use {@link #FieldMaskSerializer(ProtobufJacksonConfig)}
   */
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

  @Override
  public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
    visitor.expectStringFormat(typeHint);
  }
}
