package com.hubspot.jackson.datatype.protobuf.builtin.serializers;

import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Value;
import com.hubspot.jackson.datatype.protobuf.ProtobufJacksonConfig;
import com.hubspot.jackson.datatype.protobuf.ProtobufSerializer;
import java.util.Map;
import java.util.Map.Entry;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;

public class ValueSerializer extends ProtobufSerializer<Value> {

  /**
   * @deprecated use {@link #ValueSerializer(ProtobufJacksonConfig)}
   */
  @Deprecated
  public ValueSerializer() {
    this(ProtobufJacksonConfig.getDefaultInstance());
  }

  public ValueSerializer(ProtobufJacksonConfig config) {
    super(Value.class, config);
  }

  @Override
  public void serialize(
    Value value,
    JsonGenerator generator,
    SerializationContext serializerProvider
  ) throws JacksonException {
    Map<FieldDescriptor, Object> fields = value.getAllFields();
    if (fields.isEmpty()) {
      generator.writeNull();
    } else {
      // should only have 1 entry
      for (Entry<FieldDescriptor, Object> entry : fields.entrySet()) {
        writeValue(entry.getKey(), entry.getValue(), generator, serializerProvider);
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
