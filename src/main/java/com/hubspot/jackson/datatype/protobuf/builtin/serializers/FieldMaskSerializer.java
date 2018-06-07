package com.hubspot.jackson.datatype.protobuf.builtin.serializers;

import java.io.IOException;
import java.lang.reflect.Type;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.google.protobuf.FieldMask;
import com.google.protobuf.util.FieldMaskUtil;
import com.hubspot.jackson.datatype.protobuf.ProtobufSerializer;

public class FieldMaskSerializer extends ProtobufSerializer<FieldMask> {

  public FieldMaskSerializer() {
    super(FieldMask.class);
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

  @Override
  public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
    return createSchemaNode("string", true);
  }
}
