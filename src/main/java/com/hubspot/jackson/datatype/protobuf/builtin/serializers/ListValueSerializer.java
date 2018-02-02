package com.hubspot.jackson.datatype.protobuf.builtin.serializers;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.ListValue;
import com.google.protobuf.Value;
import com.hubspot.jackson.datatype.protobuf.ProtobufSerializer;

public class ListValueSerializer extends ProtobufSerializer<ListValue> {

  public ListValueSerializer() {
    super(ListValue.class);
  }

  @Override
  public void serialize(
          ListValue listValue,
          JsonGenerator generator,
          SerializerProvider serializerProvider
  ) throws IOException {
    FieldDescriptor field = listValue.getDescriptorForType().findFieldByName("value");
    generator.writeStartArray();
    for (Value value : listValue.getValuesList()) {
      writeValue(field, value, generator, serializerProvider);
    }
    generator.writeEndArray();
  }
}
