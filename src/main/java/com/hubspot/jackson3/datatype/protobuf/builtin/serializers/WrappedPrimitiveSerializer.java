package com.hubspot.jackson3.datatype.protobuf.builtin.serializers;

import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.MessageOrBuilder;
import com.hubspot.jackson3.datatype.protobuf.ProtobufJacksonConfig;
import com.hubspot.jackson3.datatype.protobuf.ProtobufSerializer;
import com.hubspot.jackson3.datatype.protobuf.internal.FieldSchemaGenerator;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;

public class WrappedPrimitiveSerializer<T extends MessageOrBuilder>
  extends ProtobufSerializer<T> {

  private final FieldDescriptor valueField;

  @SuppressWarnings("unchecked")
  public WrappedPrimitiveSerializer(T defaultInstance, ProtobufJacksonConfig config) {
    super((Class<T>) defaultInstance.getClass(), config);
    this.valueField = defaultInstance.getDescriptorForType().findFieldByName("value");
  }

  @Override
  public void serialize(
    MessageOrBuilder message,
    JsonGenerator generator,
    SerializationContext serializationContext
  ) {
    Object value = message.getField(valueField);
    writeValue(valueField, value, generator, serializationContext);
  }

  @Override
  public void acceptJsonFormatVisitor(
    JsonFormatVisitorWrapper visitor,
    JavaType typeHint
  ) {
    new FieldSchemaGenerator(valueField, getConfig())
      .acceptJsonFormatVisitor(visitor, typeHint);
  }
}
