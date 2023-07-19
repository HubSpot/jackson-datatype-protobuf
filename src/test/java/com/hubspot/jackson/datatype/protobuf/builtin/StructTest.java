package com.hubspot.jackson.datatype.protobuf.builtin;

import static com.hubspot.jackson.datatype.protobuf.util.ObjectMapperHelper.camelCase;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.protobuf.ListValue;
import com.google.protobuf.NullValue;
import com.google.protobuf.Struct;
import com.google.protobuf.Value;
import com.hubspot.jackson.datatype.protobuf.util.BuiltInProtobufs.HasStruct;
import java.io.IOException;
import java.util.Map;
import org.junit.Test;

public class StructTest {

  @Test
  public void itWritesAllStructValueTypes() throws IOException {
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
    HasStruct message = HasStruct.newBuilder().setStruct(struct).build();
    String json = camelCase().writeValueAsString(message);
    JsonNode node = camelCase().readTree(json).get("struct");
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
  public void itDoesntSetStructWhenValueIsNull() throws IOException {
    String json = "{\"struct\":null}";
    HasStruct valueWrapper = camelCase().readValue(json, HasStruct.class);
    assertThat(valueWrapper.hasStruct()).isFalse();
  }

  @Test
  public void itSetsEmptyMapWhenStructIsEmpty() throws IOException {
    String json = "{\"struct\":{}}";
    HasStruct valueWrapper = camelCase().readValue(json, HasStruct.class);
    assertThat(valueWrapper.hasStruct()).isTrue();
    assertThat(valueWrapper.getStruct().getFieldsMap()).isEmpty();
  }

  @Test
  public void itReadsAllStructValueTypes() throws IOException {
    String json =
      "{\"struct\":{\"null\":null,\"number\":1.5,\"string\":\"test\",\"boolean\":true,\"struct\":{\"key\":\"nested\"},\"list\":[\"nested\"]}}";
    HasStruct message = camelCase().readValue(json, HasStruct.class);
    assertThat(message.hasStruct()).isTrue();

    Map<String, Value> map = message.getStruct().getFieldsMap();
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
    assertThat(map.get("list")).isEqualTo(Value.newBuilder().setListValue(list).build());
  }
}
