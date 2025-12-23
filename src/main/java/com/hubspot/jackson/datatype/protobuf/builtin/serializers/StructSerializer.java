package com.hubspot.jackson.datatype.protobuf.builtin.serializers;

import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Struct;
import com.google.protobuf.Value;
import com.hubspot.jackson.datatype.protobuf.ProtobufJacksonConfig;
import com.hubspot.jackson.datatype.protobuf.ProtobufSerializer;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.DatabindException;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import tools.jackson.databind.jsonFormatVisitors.JsonMapFormatVisitor;

public class StructSerializer extends ProtobufSerializer<Struct> {

  private static final FieldDescriptor FIELDS_FIELD = Struct
    .getDescriptor()
    .findFieldByName("fields");

  /**
   * @deprecated use {@link #StructSerializer(ProtobufJacksonConfig)}
   */
  @Deprecated
  public StructSerializer() {
    this(ProtobufJacksonConfig.getDefaultInstance());
  }

  public StructSerializer(ProtobufJacksonConfig config) {
    super(Struct.class, config);
  }

  @Override
  public void serialize(
    Struct struct,
    JsonGenerator generator,
    SerializationContext serializerProvider
  ) throws JacksonException {
    writeMap(FIELDS_FIELD, struct.getField(FIELDS_FIELD), generator, serializerProvider);
  }

  @Override
  public void acceptJsonFormatVisitor(
    JsonFormatVisitorWrapper visitor,
    JavaType typeHint
  ) throws DatabindException {
    JavaType keyType = visitor.getContext().constructType(String.class);
    JavaType valueType = visitor.getContext().constructType(Value.class);
    JavaType mapType = visitor
      .getContext()
      .getTypeFactory()
      .constructMapLikeType(typeHint.getRawClass(), keyType, valueType);

    JsonMapFormatVisitor mapVisitor = visitor.expectMapFormat(mapType);
    if (mapVisitor != null) {
      mapVisitor.keyFormat(JsonFormatVisitorWrapper::expectStringFormat, keyType);
      mapVisitor.valueFormat(new ValueSerializer(getConfig()), valueType);
    }
  }
}
