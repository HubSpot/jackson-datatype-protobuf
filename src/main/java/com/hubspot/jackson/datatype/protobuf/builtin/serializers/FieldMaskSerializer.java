package com.hubspot.jackson.datatype.protobuf.builtin.serializers;

import com.google.protobuf.FieldMask;
import com.google.protobuf.util.FieldMaskUtil;
import com.hubspot.jackson.datatype.protobuf.ProtobufJacksonConfig;
import com.hubspot.jackson.datatype.protobuf.ProtobufSerializer;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;

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
    SerializationContext serializerProvider
  ) throws JacksonException {
    generator.writeString(FieldMaskUtil.toJsonString(fieldMask));
  }

  @Override
  public void acceptJsonFormatVisitor(
    JsonFormatVisitorWrapper visitor,
    JavaType typeHint
  ) {
    visitor.expectStringFormat(typeHint);
  }
}
