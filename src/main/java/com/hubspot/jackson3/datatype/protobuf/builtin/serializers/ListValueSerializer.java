package com.hubspot.jackson3.datatype.protobuf.builtin.serializers;

import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.ListValue;
import com.google.protobuf.Value;
import com.hubspot.jackson3.datatype.protobuf.ProtobufJacksonConfig;
import com.hubspot.jackson3.datatype.protobuf.ProtobufSerializer;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.jsonFormatVisitors.JsonArrayFormatVisitor;
import tools.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;

public class ListValueSerializer extends ProtobufSerializer<ListValue> {

  private static final FieldDescriptor VALUES_FIELD = ListValue
    .getDescriptor()
    .findFieldByName("values");

  public ListValueSerializer(ProtobufJacksonConfig config) {
    super(ListValue.class, config);
  }

  @Override
  public void serialize(
    ListValue listValue,
    JsonGenerator generator,
    SerializationContext serializationContext
  ) {
    generator.writeStartArray();
    for (Value value : listValue.getValuesList()) {
      writeValue(VALUES_FIELD, value, generator, serializationContext);
    }
    generator.writeEndArray();
  }

  @Override
  public void acceptJsonFormatVisitor(
    JsonFormatVisitorWrapper visitor,
    JavaType typeHint
  ) {
    JavaType elementType = visitor.getContext().constructType(Value.class);
    JavaType arrayType = visitor
      .getContext()
      .getTypeFactory()
      .constructArrayType(elementType);
    JsonArrayFormatVisitor itemVisitor = visitor.expectArrayFormat(arrayType);
    if (itemVisitor != null) {
      itemVisitor.itemsFormat(new ProtobufValueSerializer(getConfig()), elementType);
    }
  }
}
