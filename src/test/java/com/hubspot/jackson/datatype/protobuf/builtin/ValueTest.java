package com.hubspot.jackson.datatype.protobuf.builtin;

import static com.hubspot.jackson.datatype.protobuf.util.ObjectMapperHelper.camelCase;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.assertj.core.api.Assertions.fail;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.protobuf.ListValue;
import com.google.protobuf.NullValue;
import com.google.protobuf.Struct;
import com.google.protobuf.Value;
import com.hubspot.jackson.datatype.protobuf.util.BuiltInProtobufs.HasValue;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import org.junit.Test;

public class ValueTest {

  @Test
  public void itWritesNullValue() throws IOException {
    HasValue message = HasValue
      .newBuilder()
      .setValue(Value.newBuilder().setNullValue(NullValue.NULL_VALUE).build())
      .build();
    String json = camelCase().writeValueAsString(message);
    assertThat(json).isEqualTo("{\"value\":null}");
  }

  @Test
  public void itWritesNumberValue() throws IOException {
    HasValue message = HasValue
      .newBuilder()
      .setValue(Value.newBuilder().setNumberValue(1.5d).build())
      .build();
    String json = camelCase().writeValueAsString(message);
    assertThat(json).isEqualTo("{\"value\":1.5}");
  }

  @Test
  public void itWritesStringValue() throws IOException {
    HasValue message = HasValue
      .newBuilder()
      .setValue(Value.newBuilder().setStringValue("test").build())
      .build();
    String json = camelCase().writeValueAsString(message);
    assertThat(json).isEqualTo("{\"value\":\"test\"}");
  }

  @Test
  public void itWritesBooleanValue() throws IOException {
    HasValue message = HasValue
      .newBuilder()
      .setValue(Value.newBuilder().setBoolValue(true).build())
      .build();
    String json = camelCase().writeValueAsString(message);
    assertThat(json).isEqualTo("{\"value\":true}");
  }

  @Test
  public void itWritesStructValue() throws IOException {
    Struct struct = Struct
      .newBuilder()
      .putFields("key", Value.newBuilder().setStringValue("value").build())
      .build();
    HasValue message = HasValue
      .newBuilder()
      .setValue(Value.newBuilder().setStructValue(struct).build())
      .build();
    String json = camelCase().writeValueAsString(message);
    assertThat(json).isEqualTo("{\"value\":{\"key\":\"value\"}}");
  }

  @Test
  public void itWritesListValue() throws IOException {
    ListValue list = ListValue
      .newBuilder()
      .addValues(Value.newBuilder().setStringValue("test").build())
      .build();
    HasValue message = HasValue
      .newBuilder()
      .setValue(Value.newBuilder().setListValue(list).build())
      .build();
    String json = camelCase().writeValueAsString(message);
    assertThat(json).isEqualTo("{\"value\":[\"test\"]}");
  }

  @Test
  public void itWritesMixedStruct() throws IOException {
    Value nestedValue = Value.newBuilder().setStringValue("nested").build();
    Struct nestedStruct = Struct.newBuilder().putFields("key", nestedValue).build();
    ListValue list = ListValue.newBuilder().addValues(nestedValue).build();
    Struct struct = Struct
      .newBuilder()
      .putFields("null", Value.newBuilder().setNullValue(NullValue.NULL_VALUE).build())
      .putFields("number", Value.newBuilder().setNumberValue(1.5d).build())
      .putFields("string", Value.newBuilder().setStringValue("test").build())
      .putFields("boolean", Value.newBuilder().setBoolValue(true).build())
      .putFields("struct", Value.newBuilder().setStructValue(nestedStruct).build())
      .putFields("list", Value.newBuilder().setListValue(list).build())
      .build();
    HasValue message = HasValue
      .newBuilder()
      .setValue(Value.newBuilder().setStructValue(struct).build())
      .build();
    String json = camelCase().writeValueAsString(message);
    JsonNode node = camelCase().readTree(json).get("value");
    assertThat(node.get("null").isNull()).isTrue();
    assertThat(node.get("number").isNumber()).isTrue();
    assertThat(node.get("number").numberValue().doubleValue()).isEqualTo(1.5d);
    assertThat(node.get("string").isTextual()).isTrue();
    assertThat(node.get("string").textValue()).isEqualTo("test");
    assertThat(node.get("boolean").isBoolean()).isTrue();
    assertThat(node.get("boolean").booleanValue()).isTrue();
    assertThat(node.get("struct").isObject()).isTrue();
    assertThat(node.get("struct").size()).isEqualTo(1);
    assertThat(node.get("struct").get("key").isTextual()).isTrue();
    assertThat(node.get("struct").get("key").textValue()).isEqualTo("nested");
    assertThat(node.get("list").isArray()).isTrue();
    assertThat(node.get("list").size()).isEqualTo(1);
    assertThat(node.get("list").get(0).isTextual()).isTrue();
    assertThat(node.get("list").get(0).textValue()).isEqualTo("nested");
  }

  @Test
  public void itWritesMixedListValue() throws IOException {
    Value nestedValue = Value.newBuilder().setStringValue("nested").build();
    Struct struct = Struct.newBuilder().putFields("key", nestedValue).build();
    ListValue nestedList = ListValue.newBuilder().addValues(nestedValue).build();
    ListValue list = ListValue
      .newBuilder()
      .addValues(Value.newBuilder().setNullValue(NullValue.NULL_VALUE).build())
      .addValues(Value.newBuilder().setNumberValue(1.5d).build())
      .addValues(Value.newBuilder().setStringValue("test").build())
      .addValues(Value.newBuilder().setBoolValue(true).build())
      .addValues(Value.newBuilder().setStructValue(struct).build())
      .addValues(Value.newBuilder().setListValue(nestedList).build())
      .build();
    HasValue message = HasValue
      .newBuilder()
      .setValue(Value.newBuilder().setListValue(list).build())
      .build();
    String json = camelCase().writeValueAsString(message);
    assertThat(json)
      .isEqualTo(
        "{\"value\":[null,1.5,\"test\",true,{\"key\":\"nested\"},[\"nested\"]]}"
      );
  }

  @Test
  public void itReadsNullValue() throws IOException {
    String json = "{\"value\":null}";
    HasValue valueWrapper = camelCase().readValue(json, HasValue.class);
    assertThat(valueWrapper.hasValue()).isTrue();

    Value value = valueWrapper.getValue();
    switch (value.getKindCase()) {
      case NULL_VALUE:
        assertThat(value.getNullValue()).isEqualTo(NullValue.NULL_VALUE);
        break;
      default:
        fail("Unexpected value kind: " + value.getKindCase());
    }
  }

  @Test
  public void itReadsIntegralValue() throws IOException {
    String json = "{\"value\":1}";
    HasValue valueWrapper = camelCase().readValue(json, HasValue.class);
    assertThat(valueWrapper.hasValue()).isTrue();

    Value value = valueWrapper.getValue();
    switch (value.getKindCase()) {
      case NUMBER_VALUE:
        assertThat(value.getNumberValue()).isEqualTo(1.0d);
        break;
      default:
        fail("Unexpected value kind: " + value.getKindCase());
    }
  }

  @Test
  public void itReadsFloatingPointValue() throws IOException {
    String json = "{\"value\":1.5}";
    HasValue valueWrapper = camelCase().readValue(json, HasValue.class);
    assertThat(valueWrapper.hasValue()).isTrue();

    Value value = valueWrapper.getValue();
    switch (value.getKindCase()) {
      case NUMBER_VALUE:
        assertThat(value.getNumberValue()).isEqualTo(1.5d);
        break;
      default:
        fail("Unexpected value kind: " + value.getKindCase());
    }
  }

  @Test
  public void itReadsStringValue() throws IOException {
    String json = "{\"value\":\"test\"}";
    HasValue valueWrapper = camelCase().readValue(json, HasValue.class);
    assertThat(valueWrapper.hasValue()).isTrue();

    Value value = valueWrapper.getValue();
    switch (value.getKindCase()) {
      case STRING_VALUE:
        assertThat(value.getStringValue()).isEqualTo("test");
        break;
      default:
        fail("Unexpected value kind: " + value.getKindCase());
    }
  }

  @Test
  public void itReadsBooleanValue() throws IOException {
    String json = "{\"value\":true}";
    HasValue valueWrapper = camelCase().readValue(json, HasValue.class);
    assertThat(valueWrapper.hasValue()).isTrue();

    Value value = valueWrapper.getValue();
    switch (value.getKindCase()) {
      case BOOL_VALUE:
        assertThat(value.getBoolValue()).isTrue();
        break;
      default:
        fail("Unexpected value kind: " + value.getKindCase());
    }
  }

  @Test
  public void itReadsStructValue() throws IOException {
    String json = "{\"value\":{\"key\":\"value\"}}";
    HasValue valueWrapper = camelCase().readValue(json, HasValue.class);
    assertThat(valueWrapper.hasValue()).isTrue();

    Value value = valueWrapper.getValue();
    switch (value.getKindCase()) {
      case STRUCT_VALUE:
        Entry<String, Value> entry = entry(
          "key",
          Value.newBuilder().setStringValue("value").build()
        );
        assertThat(value.getStructValue().getFieldsMap()).containsExactly(entry);
        break;
      default:
        fail("Unexpected value kind: " + value.getKindCase());
    }
  }

  @Test
  public void itReadsListValue() throws IOException {
    String json = "{\"value\":[\"test\"]}";
    HasValue valueWrapper = camelCase().readValue(json, HasValue.class);
    assertThat(valueWrapper.hasValue()).isTrue();

    Value value = valueWrapper.getValue();
    ListValue list = ListValue
      .newBuilder()
      .addValues(Value.newBuilder().setStringValue("test").build())
      .build();
    switch (value.getKindCase()) {
      case LIST_VALUE:
        assertThat(value.getListValue()).isEqualTo(list);
        break;
      default:
        fail("Unexpected value kind: " + value.getKindCase());
    }
  }

  @Test
  public void itReadsMixedStruct() throws IOException {
    String json =
      "{\"value\":{\"null\":null,\"number\":1.5,\"string\":\"test\",\"boolean\":true,\"struct\":{\"key\":\"nested\"},\"list\":[\"nested\"]}}";
    HasValue message = camelCase().readValue(json, HasValue.class);
    assertThat(message.hasValue()).isTrue();
    Value value = message.getValue();
    switch (value.getKindCase()) {
      case STRUCT_VALUE:
        Map<String, Value> map = value.getStructValue().getFieldsMap();
        Value nested = Value.newBuilder().setStringValue("nested").build();
        Struct nestedStruct = Struct.newBuilder().putFields("key", nested).build();
        ListValue list = ListValue.newBuilder().addValues(nested).build();
        assertThat(map.size()).isEqualTo(6);
        assertThat(map.get("null"))
          .isEqualTo(Value.newBuilder().setNullValue(NullValue.NULL_VALUE).build());
        assertThat(map.get("number"))
          .isEqualTo(Value.newBuilder().setNumberValue(1.5).build());
        assertThat(map.get("string"))
          .isEqualTo(Value.newBuilder().setStringValue("test").build());
        assertThat(map.get("boolean"))
          .isEqualTo(Value.newBuilder().setBoolValue(true).build());
        assertThat(map.get("struct"))
          .isEqualTo(Value.newBuilder().setStructValue(nestedStruct).build());
        assertThat(map.get("list"))
          .isEqualTo(Value.newBuilder().setListValue(list).build());
        break;
      default:
        fail("Unexpected value kind: " + value.getKindCase());
    }
  }

  @Test
  public void itReadsMixedListValue() throws IOException {
    String json =
      "{\"value\":[null,1.5,\"test\",true,{\"key\":\"nested\"},[\"nested\"]]}";
    HasValue message = camelCase().readValue(json, HasValue.class);
    assertThat(message.hasValue()).isTrue();
    Value value = message.getValue();
    switch (value.getKindCase()) {
      case LIST_VALUE:
        ListValue list = value.getListValue();
        Value nested = Value.newBuilder().setStringValue("nested").build();
        Struct struct = Struct.newBuilder().putFields("key", nested).build();
        ListValue nestedList = ListValue.newBuilder().addValues(nested).build();
        assertThat(list.getValuesCount()).isEqualTo(6);
        assertThat(list.getValues(0))
          .isEqualTo(Value.newBuilder().setNullValue(NullValue.NULL_VALUE).build());
        assertThat(list.getValues(1))
          .isEqualTo(Value.newBuilder().setNumberValue(1.5).build());
        assertThat(list.getValues(2))
          .isEqualTo(Value.newBuilder().setStringValue("test").build());
        assertThat(list.getValues(3))
          .isEqualTo(Value.newBuilder().setBoolValue(true).build());
        assertThat(list.getValues(4))
          .isEqualTo(Value.newBuilder().setStructValue(struct).build());
        assertThat(list.getValues(5))
          .isEqualTo(Value.newBuilder().setListValue(nestedList).build());
        break;
      default:
        fail("Unexpected value kind: " + value.getKindCase());
    }
  }
}
