package com.hubspot.jackson.datatype.protobuf.builtin.serializers;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Value;
import com.hubspot.jackson.datatype.protobuf.ProtobufSerializer;

public class ValueSerializer extends ProtobufSerializer<Value> {

  public ValueSerializer() {
    super(Value.class);
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

  @Override
  public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
    // don't think we can do any better here?
    visitor.expectAnyFormat(typeHint);
  }

  @Override
  public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
    // anything better to do here?
    return newObjectNode().set("type", stringArray("null", "number", "string", "boolean", "object", "array"));
  }

  private static ObjectNode newObjectNode() {
    return JsonNodeFactory.instance.objectNode();
  }

  private static ArrayNode stringArray(String... values) {
    ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();
    for (String value : values) {
      arrayNode.add(JsonNodeFactory.instance.textNode(value));
    }

    return arrayNode;
  }
}
