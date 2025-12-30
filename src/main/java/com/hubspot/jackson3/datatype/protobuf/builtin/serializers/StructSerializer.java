package com.hubspot.jackson3.datatype.protobuf.builtin.serializers;

import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Struct;
import com.google.protobuf.Value;
import com.hubspot.jackson3.datatype.protobuf.ProtobufJacksonConfig;
import com.hubspot.jackson3.datatype.protobuf.ProtobufSerializer;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import tools.jackson.databind.jsonFormatVisitors.JsonMapFormatVisitor;

public class StructSerializer extends ProtobufSerializer<Struct> {

  private static final FieldDescriptor FIELDS_FIELD = Struct
    .getDescriptor()
    .findFieldByName("fields");

  public StructSerializer(ProtobufJacksonConfig config) {
    super(Struct.class, config);
  }

  @Override
  public void serialize(
    Struct struct,
    JsonGenerator generator,
    SerializationContext serializationContext
  ) {
    writeMap(
      FIELDS_FIELD,
      struct.getField(FIELDS_FIELD),
      generator,
      serializationContext
    );
  }

  @Override
  public void acceptJsonFormatVisitor(
    JsonFormatVisitorWrapper visitor,
    JavaType typeHint
  ) {
    JavaType keyType = visitor.getContext().constructType(String.class);
    JavaType valueType = visitor.getContext().constructType(Value.class);
    JavaType mapType = visitor
      .getContext()
      .getTypeFactory()
      .constructMapLikeType(typeHint.getRawClass(), keyType, valueType);

    JsonMapFormatVisitor mapVisitor = visitor.expectMapFormat(mapType);
    if (mapVisitor != null) {
      mapVisitor.keyFormat(JsonFormatVisitorWrapper::expectStringFormat, keyType);
      mapVisitor.valueFormat(new ProtobufValueSerializer(getConfig()), valueType);
    }
  }
}
