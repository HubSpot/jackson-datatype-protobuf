package com.hubspot.jackson.test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.hubspot.jackson.datatype.protobuf.ProtobufModule;
import com.hubspot.jackson.test.util.ProtobufCreator;
import com.hubspot.jackson.test.util.TestProtobuf.PropertyNamingCamelCased;
import com.hubspot.jackson.test.util.TestProtobuf.PropertyNamingSnakeCased;

import org.junit.Test;

import java.util.List;

import static com.hubspot.jackson.test.util.ObjectMapperHelper.camelCase;
import static com.hubspot.jackson.test.util.ObjectMapperHelper.toTree;
import static com.hubspot.jackson.test.util.ObjectMapperHelper.underscore;
import static org.assertj.core.api.Assertions.assertThat;

public class PropertyNamingTest {

  @Test
  public void testSingleSnakeCaseToCamelCase() {
    PropertyNamingSnakeCased message = ProtobufCreator.create(PropertyNamingSnakeCased.class);

    JsonNode tree = toTree(camelCase(), message);

    assertThat(tree.isObject()).isTrue();
    assertThat(tree.size()).isEqualTo(1);
    assertThat(tree.get("stringAttribute")).isNotNull();
    assertThat(tree.get("stringAttribute").textValue()).isEqualTo(message.getStringAttribute());
  }

  @Test
  public void testMultipleSnakeCaseToCamelCase() {
    List<PropertyNamingSnakeCased> messages = ProtobufCreator.create(PropertyNamingSnakeCased.class, 10);

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
    PropertyNamingSnakeCased message = ProtobufCreator.create(PropertyNamingSnakeCased.class);

    JsonNode tree = toTree(underscore(), message);

    assertThat(tree.isObject()).isTrue();
    assertThat(tree.size()).isEqualTo(1);
    assertThat(tree.get("string_attribute")).isNotNull();
    assertThat(tree.get("string_attribute").textValue()).isEqualTo(message.getStringAttribute());
  }

  @Test
  public void testMultipleUnderscore() {
    List<PropertyNamingSnakeCased> messages = ProtobufCreator.create(PropertyNamingSnakeCased.class, 10);

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

  @Test
  public void testSingleStillCamelCase() {
    PropertyNamingCamelCased message = ProtobufCreator.create(PropertyNamingCamelCased.class);

    @SuppressWarnings("serial")
    ObjectMapper mapper = new ObjectMapper().registerModule(new ProtobufModule()).setPropertyNamingStrategy(
        new PropertyNamingStrategy.PropertyNamingStrategyBase() {
          @Override
          public String translate(String propertyName) {
            return propertyName;
          }
        });

    JsonNode tree = toTree(mapper, message);

    assertThat(tree.isObject()).isTrue();
    assertThat(tree.size()).isEqualTo(1);
    assertThat(tree.get("stringAttribute")).isNotNull();
    assertThat(tree.get("stringAttribute").textValue()).isEqualTo(message.getStringAttribute());
  }

  @Test
  public void testMultipleSillCamelCase() {
    List<PropertyNamingCamelCased> messages = ProtobufCreator.create(PropertyNamingCamelCased.class, 10);

    @SuppressWarnings("serial")
    ObjectMapper mapper = new ObjectMapper().registerModule(new ProtobufModule()).setPropertyNamingStrategy(
        new PropertyNamingStrategy.PropertyNamingStrategyBase() {
          @Override
          public String translate(String propertyName) {
            return propertyName;
          }
        });

    JsonNode tree = toTree(mapper, messages);

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

}
