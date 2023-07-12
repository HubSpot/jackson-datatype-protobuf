package com.hubspot.jackson.datatype.protobuf.builtin.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Value;
import com.hubspot.jackson.datatype.protobuf.ProtobufJacksonConfig;
import com.hubspot.jackson.datatype.protobuf.ProtobufSerializer;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

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
          SerializerProvider serializerProvider
  ) throws IOException {
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
}
