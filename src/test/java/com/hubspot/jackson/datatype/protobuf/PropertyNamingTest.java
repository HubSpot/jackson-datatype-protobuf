package com.hubspot.jackson.datatype.protobuf;

import static com.hubspot.jackson.datatype.protobuf.util.ObjectMapperHelper.camelCase;
import static com.hubspot.jackson.datatype.protobuf.util.ObjectMapperHelper.toTree;
import static com.hubspot.jackson.datatype.protobuf.util.ObjectMapperHelper.underscore;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.hubspot.jackson.datatype.protobuf.util.ProtobufCreator;
import com.hubspot.jackson.datatype.protobuf.util.TestProtobuf.PropertyNamingCamelCased;
import com.hubspot.jackson.datatype.protobuf.util.TestProtobuf.PropertyNamingSnakeCased;

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
  public void testMultipleStillCamelCase() {
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

  /**
   * If the protobuf property is underscore, we expect the JSON field name to be camelcase.
   * But if the JSON field name is already underscore, we should still accept it.
   */
  @Test
  public void itAcceptsUnderscoreNameForCamelcaseProperty() throws IOException {
    String json = "{\"string_attribute\":\"test\"}";
    PropertyNamingSnakeCased message = camelCase().readValue(json, PropertyNamingSnakeCased.class);

    assertThat(message.getStringAttribute()).isEqualTo("test");
  }
}
