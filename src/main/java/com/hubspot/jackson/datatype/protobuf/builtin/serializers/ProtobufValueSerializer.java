package com.hubspot.jackson.datatype.protobuf.builtin.serializers;

import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Value;
import com.hubspot.jackson.datatype.protobuf.ProtobufJacksonConfig;
import com.hubspot.jackson.datatype.protobuf.ProtobufSerializer;
import java.util.Map;
import java.util.Map.Entry;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;

public class ProtobufValueSerializer extends ProtobufSerializer<Value> {

  public ProtobufValueSerializer(ProtobufJacksonConfig config) {
    super(Value.class, config);
  }

  @Override
  public void serialize(
    Value value,
    JsonGenerator generator,
    SerializationContext serializationContext
  ) {
    Map<FieldDescriptor, Object> fields = value.getAllFields();
    if (fields.isEmpty()) {
      generator.writeNull();
    } else {
      // should only have 1 entry
      for (Entry<FieldDescriptor, Object> entry : fields.entrySet()) {
        writeValue(entry.getKey(), entry.getValue(), generator, serializationContext);
      }
    }
  }

  @Override
  public void acceptJsonFormatVisitor(
    JsonFormatVisitorWrapper visitor,
    JavaType typeHint
  ) {
    // don't think we can do any better here since a Value is arbitrary json
    visitor.expectAnyFormat(typeHint);
  }
}
