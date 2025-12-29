package com.hubspot.jackson.datatype.protobuf.builtin.deserializers;

import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Message;
import com.google.protobuf.Struct;
import com.hubspot.jackson.datatype.protobuf.ProtobufDeserializer;
import java.util.List;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;

public class StructDeserializer extends ProtobufDeserializer<Struct, Struct.Builder> {

  private static final FieldDescriptor FIELDS_FIELD = Struct
    .getDescriptor()
    .findFieldByName("fields");

  public StructDeserializer() {
    super(Struct.class);
  }

  @Override
  protected void populate(
    Struct.Builder builder,
    JsonParser parser,
    DeserializationContext context
  ) {
    List<Message> entries = readMap(builder, FIELDS_FIELD, parser, context);
    for (Message entry : entries) {
      builder.addRepeatedField(FIELDS_FIELD, entry);
    }
  }
}
