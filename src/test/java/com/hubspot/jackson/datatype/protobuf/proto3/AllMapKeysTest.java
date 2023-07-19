package com.hubspot.jackson.datatype.protobuf.proto3;

import static com.hubspot.jackson.datatype.protobuf.util.ObjectMapperHelper.camelCase;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hubspot.jackson.datatype.protobuf.util.BuiltInProtobufs.HasAllMapKeys;
import java.io.IOException;
import org.junit.Test;

public class AllMapKeysTest {

  @Test
  public void itWritesAllMapKeysWhenPopulated() throws IOException {
    HasAllMapKeys message = hasAllMapKeys();
    String json = camelCase().writeValueAsString(message);
    JsonNode node = camelCase().readTree(json);
    assertThat(node).isEqualTo(hasAllMapKeysNode());
  }

  @Test
  public void itWritesEmptyMapsWhenNotPopulated() throws IOException {
    HasAllMapKeys message = HasAllMapKeys.newBuilder().build();
    String json = camelCase().writeValueAsString(message);
    JsonNode node = camelCase().readTree(json);
    assertThat(node).isEqualTo(hasEmptyMapsNode());
  }

  @Test
  public void itReadsAllMapKeysWhenPopulated() throws IOException {
    String json = camelCase().writeValueAsString(hasAllMapKeysNode());
    HasAllMapKeys message = camelCase().readValue(json, HasAllMapKeys.class);
    assertThat(message).isEqualTo(hasAllMapKeys());
  }

  @Test
  public void itDoesntSetMapFieldsWhenEmpty() throws IOException {
    String json = camelCase().writeValueAsString(hasEmptyMapsNode());
    HasAllMapKeys message = camelCase().readValue(json, HasAllMapKeys.class);
    assertThat(message).isEqualTo(HasAllMapKeys.getDefaultInstance());
  }

  @Test
  public void itDoesntSetMapFieldsWhenNull() throws IOException {
    String json = camelCase().writeValueAsString(hasNullMapsNode());
    HasAllMapKeys message = camelCase().readValue(json, HasAllMapKeys.class);
    assertThat(message).isEqualTo(HasAllMapKeys.getDefaultInstance());
  }

  private static HasAllMapKeys hasAllMapKeys() {
    return HasAllMapKeys
      .newBuilder()
      .putInt32Map(1, "int32")
      .putInt64Map(2, "int64")
      .putUint32Map(3, "uint32")
      .putUint64Map(4, "uint64")
      .putSint32Map(5, "sint32")
      .putSint64Map(6, "sint64")
      .putFixed32Map(7, "fixed32")
      .putFixed64Map(8, "fixed64")
      .putSfixed32Map(9, "sfixed32")
      .putSfixed64Map(10, "sfixed64")
      .putBoolMap(true, "bool")
      .putStringMap("key", "value")
      .build();
  }

  private static ObjectNode hasAllMapKeysNode() {
    ObjectNode node = newObjectNode();
    node.set("int32Map", newObjectNode().put("1", "int32"));
    node.set("int64Map", newObjectNode().put("2", "int64"));
    node.set("uint32Map", newObjectNode().put("3", "uint32"));
    node.set("uint64Map", newObjectNode().put("4", "uint64"));
    node.set("sint32Map", newObjectNode().put("5", "sint32"));
    node.set("sint64Map", newObjectNode().put("6", "sint64"));
    node.set("fixed32Map", newObjectNode().put("7", "fixed32"));
    node.set("fixed64Map", newObjectNode().put("8", "fixed64"));
    node.set("sfixed32Map", newObjectNode().put("9", "sfixed32"));
    node.set("sfixed64Map", newObjectNode().put("10", "sfixed64"));
    node.set("boolMap", newObjectNode().put("true", "bool"));
    node.set("stringMap", newObjectNode().put("key", "value"));
    return node;
  }

  private static ObjectNode hasEmptyMapsNode() {
    ObjectNode node = newObjectNode();
    node.set("int32Map", newObjectNode());
    node.set("int64Map", newObjectNode());
    node.set("uint32Map", newObjectNode());
    node.set("uint64Map", newObjectNode());
    node.set("sint32Map", newObjectNode());
    node.set("sint64Map", newObjectNode());
    node.set("fixed32Map", newObjectNode());
    node.set("fixed64Map", newObjectNode());
    node.set("sfixed32Map", newObjectNode());
    node.set("sfixed64Map", newObjectNode());
    node.set("boolMap", newObjectNode());
    node.set("stringMap", newObjectNode());
    return node;
  }

  private static ObjectNode hasNullMapsNode() {
    ObjectNode node = newObjectNode();
    node.set("int32Map", NullNode.getInstance());
    node.set("int64Map", NullNode.getInstance());
    node.set("uint32Map", NullNode.getInstance());
    node.set("uint64Map", NullNode.getInstance());
    node.set("sint32Map", NullNode.getInstance());
    node.set("sint64Map", NullNode.getInstance());
    node.set("fixed32Map", NullNode.getInstance());
    node.set("fixed64Map", NullNode.getInstance());
    node.set("sfixed32Map", NullNode.getInstance());
    node.set("sfixed64Map", NullNode.getInstance());
    node.set("boolMap", NullNode.getInstance());
    node.set("stringMap", NullNode.getInstance());
    return node;
  }

  private static ObjectNode newObjectNode() {
    return camelCase().createObjectNode();
  }
}
