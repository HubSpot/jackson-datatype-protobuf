package com.hubspot.jackson.datatype.protobuf.builtin.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonArrayFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.ListValue;
import com.google.protobuf.Value;
import com.hubspot.jackson.datatype.protobuf.ProtobufJacksonConfig;
import com.hubspot.jackson.datatype.protobuf.ProtobufSerializer;
import java.io.IOException;

public class ListValueSerializer extends ProtobufSerializer<ListValue> {
  private static final FieldDescriptor VALUES_FIELD = ListValue.getDescriptor().findFieldByName("values");

  /**
   * @deprecated use {@link #ListValueSerializer(ProtobufJacksonConfig)}
   */
  @Deprecated
  public ListValueSerializer() {
    this(ProtobufJacksonConfig.getDefaultInstance());
  }

  public ListValueSerializer(ProtobufJacksonConfig config) {
    super(ListValue.class, config);
  }

  @Override
  public void serialize(
          ListValue listValue,
          JsonGenerator generator,
          SerializerProvider serializerProvider
  ) throws IOException {
    generator.writeStartArray();
    for (Value value : listValue.getValuesList()) {
      writeValue(VALUES_FIELD, value, generator, serializerProvider);
    }
    generator.writeEndArray();
  }

  @Override
  public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
    JsonArrayFormatVisitor itemVisitor = visitor.expectArrayFormat(typeHint);
    if (itemVisitor != null) {
      JavaType valueType = visitor.getProvider().constructType(Value.class);
      itemVisitor.itemsFormat(new ValueSerializer(getConfig()), valueType);
    }
  }
}
