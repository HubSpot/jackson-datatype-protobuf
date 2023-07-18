package com.hubspot.jackson.datatype.protobuf.builtin.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonMapFormatVisitor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Struct;
import com.google.protobuf.Value;
import com.hubspot.jackson.datatype.protobuf.ProtobufJacksonConfig;
import com.hubspot.jackson.datatype.protobuf.ProtobufSerializer;
import java.io.IOException;

public class StructSerializer extends ProtobufSerializer<Struct> {
  private static final FieldDescriptor FIELDS_FIELD = Struct.getDescriptor().findFieldByName("fields");

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
          SerializerProvider serializerProvider
  ) throws IOException {
    writeMap(FIELDS_FIELD, struct.getField(FIELDS_FIELD), generator, serializerProvider);
  }

  @Override
  public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
    JsonMapFormatVisitor mapVisitor = visitor.expectMapFormat(typeHint);
    if (mapVisitor != null) {
      JavaType stringType = visitor.getProvider().constructType(String.class);
      mapVisitor.keyFormat(JsonFormatVisitorWrapper::expectStringFormat, stringType);

      JavaType valueType = visitor.getProvider().constructType(Value.class);
      mapVisitor.valueFormat(new ValueSerializer(getConfig()), valueType);
    }
  }
}
