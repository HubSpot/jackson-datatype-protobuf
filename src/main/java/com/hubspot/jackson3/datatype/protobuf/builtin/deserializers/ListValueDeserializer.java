package com.hubspot.jackson3.datatype.protobuf.builtin.deserializers;

import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.ListValue;
import com.hubspot.jackson3.datatype.protobuf.ProtobufDeserializer;
import java.util.List;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;

public class ListValueDeserializer
  extends ProtobufDeserializer<ListValue, ListValue.Builder> {

  private static final FieldDescriptor VALUES_FIELD = ListValue
    .getDescriptor()
    .findFieldByName("values");

  public ListValueDeserializer() {
    super(ListValue.class);
  }

  @Override
  protected void populate(
    ListValue.Builder builder,
    JsonParser parser,
    DeserializationContext context
  ) {
    List<Object> values = readArray(builder, VALUES_FIELD, null, parser, context);
    for (Object value : values) {
      builder.addRepeatedField(VALUES_FIELD, value);
    }
  }
}
