package com.hubspot.jackson.datatype.protobuf.builtin.deserializers;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.ListValue;
import com.hubspot.jackson.datatype.protobuf.ProtobufDeserializer;

public class ListValueDeserializer extends ProtobufDeserializer<ListValue, ListValue.Builder> {
  private static final FieldDescriptor VALUES_FIELD = ListValue.getDescriptor().findFieldByName("values");

  public ListValueDeserializer() {
    super(ListValue.class);
  }

  @Override
  protected void populate(ListValue.Builder builder, JsonParser parser, DeserializationContext context) throws IOException {
    List<Object> values = readArray(builder, VALUES_FIELD, null, parser, context);
    for (Object value : values) {
      builder.addRepeatedField(VALUES_FIELD, value);
    }
  }
}
