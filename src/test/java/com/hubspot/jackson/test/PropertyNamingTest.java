package com.hubspot.jackson.test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.hubspot.jackson.test.util.ProtobufCreator;
import com.hubspot.jackson.test.util.TestProtobuf.PropertyNaming;
import org.junit.Test;

import java.util.List;

import static com.hubspot.jackson.test.util.ObjectMapperHelper.camelCase;
import static com.hubspot.jackson.test.util.ObjectMapperHelper.toTree;
import static com.hubspot.jackson.test.util.ObjectMapperHelper.underscore;
import static org.fest.assertions.api.Assertions.assertThat;

public class PropertyNamingTest {

  @Test
  public void testSingleCamelCase() {
    PropertyNaming message = ProtobufCreator.create(PropertyNaming.class);

    JsonNode tree = toTree(camelCase(), message);

    assertThat(tree.isObject()).isTrue();
    assertThat(tree.size()).isEqualTo(1);
    assertThat(tree.get("stringAttribute")).isNotNull();
    assertThat(tree.get("stringAttribute").textValue()).isEqualTo(message.getStringAttribute());
  }

  @Test
  public void testMultipleCamelCase() {
    List<PropertyNaming> messages = ProtobufCreator.create(PropertyNaming.class, 10);

    JsonNode tree = toTree(camelCase(), messages);

    assertThat(tree).isInstanceOf(ArrayNode.class);
    assertThat(tree.size()).isEqualTo(10);

    for (int i = 0; i < 10; i++) {
      JsonNode subTree = tree.get(i);

      assertThat(subTree.isObject()).isTrue();
      assertThat(subTree.size()).isEqualTo(1);
      assertThat(subTree.get("stringAttribute")).isNotNull();
      assertThat(subTree.get("stringAttribute").textValue()).isEqualTo(messages.get(i).getStringAttribute());
    }
  }

  @Test
  public void testSingleUnderscore() {
    PropertyNaming message = ProtobufCreator.create(PropertyNaming.class);

    JsonNode tree = toTree(underscore(), message);

    assertThat(tree.isObject()).isTrue();
    assertThat(tree.size()).isEqualTo(1);
    assertThat(tree.get("string_attribute")).isNotNull();
    assertThat(tree.get("string_attribute").textValue()).isEqualTo(message.getStringAttribute());
  }

  @Test
  public void testMultipleUnderscore() {
    List<PropertyNaming> messages = ProtobufCreator.create(PropertyNaming.class, 10);

    JsonNode tree = toTree(underscore(), messages);

    assertThat(tree).isInstanceOf(ArrayNode.class);
    assertThat(tree.size()).isEqualTo(10);

    for (int i = 0; i < 10; i++) {
      JsonNode subTree = tree.get(i);

      assertThat(subTree.isObject()).isTrue();
      assertThat(subTree.size()).isEqualTo(1);
      assertThat(subTree.get("string_attribute")).isNotNull();
      assertThat(subTree.get("string_attribute").textValue()).isEqualTo(messages.get(i).getStringAttribute());
    }
  }
}
