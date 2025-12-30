package com.hubspot.jackson3.datatype.protobuf.builtin.deserializers;

import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Message;
import com.google.protobuf.Message.Builder;
import com.hubspot.jackson3.datatype.protobuf.ProtobufDeserializer;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;

public class WrappedPrimitiveDeserializer<T extends Message, V extends Builder>
  extends ProtobufDeserializer<T, V> {

  public WrappedPrimitiveDeserializer(Class<T> wrapperType) {
    super(wrapperType);
  }

  @Override
  protected void populate(V builder, JsonParser parser, DeserializationContext context) {
    FieldDescriptor field = builder.getDescriptorForType().findFieldByName("value");
    Object value = readValue(builder, field, null, parser, context);
    builder.setField(field, value);
  }
}
