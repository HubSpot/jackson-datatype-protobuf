package com.hubspot.jackson.datatype.protobuf;

import static com.hubspot.jackson.datatype.protobuf.util.ObjectMapperHelper.camelCase;
import static com.hubspot.jackson.datatype.protobuf.util.ObjectMapperHelper.toTree;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.NamingBase;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.PropertyNamingStrategyBase;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.hubspot.jackson.datatype.protobuf.util.ObjectMapperHelper;
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

    JsonNode tree = toTree(ObjectMapperHelper.oldUnderscore(), message);

    assertThat(tree.isObject()).isTrue();
    assertThat(tree.size()).isEqualTo(1);
    assertThat(tree.get("string_attribute")).isNotNull();
    assertThat(tree.get("string_attribute").textValue()).isEqualTo(message.getStringAttribute());

    tree = toTree(ObjectMapperHelper.newUnderscore(), message);

    assertThat(tree.isObject()).isTrue();
    assertThat(tree.size()).isEqualTo(1);
    assertThat(tree.get("string_attribute")).isNotNull();
    assertThat(tree.get("string_attribute").textValue()).isEqualTo(message.getStringAttribute());
  }

  @Test
  public void testMultipleUnderscore() {
    List<PropertyNamingSnakeCased> messages = ProtobufCreator.create(PropertyNamingSnakeCased.class, 10);

    JsonNode tree = toTree(ObjectMapperHelper.oldUnderscore(), messages);

    assertThat(tree).isInstanceOf(ArrayNode.class);
    assertThat(tree.size()).isEqualTo(10);

    for (int i = 0; i < 10; i++) {
      JsonNode subTree = tree.get(i);

      assertThat(subTree.isObject()).isTrue();
      assertThat(subTree.size()).isEqualTo(1);
      assertThat(subTree.get("string_attribute")).isNotNull();
      assertThat(subTree.get("string_attribute").textValue()).isEqualTo(messages.get(i).getStringAttribute());
    }

    tree = toTree(ObjectMapperHelper.newUnderscore(), messages);

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
  public void testSingleStillCamelCaseUsingNamingBase() {
    PropertyNamingCamelCased message = ProtobufCreator.create(PropertyNamingCamelCased.class);

    @SuppressWarnings("serial")
    ObjectMapper mapper = new ObjectMapper()
        .registerModule(new ProtobufModule())
        .setPropertyNamingStrategy(snakeCaseNamingBase());

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

  @Test
  public void testMultipleStillCamelCaseUsingNamingBase() {
    List<PropertyNamingCamelCased> messages = ProtobufCreator.create(PropertyNamingCamelCased.class, 10);

    @SuppressWarnings("serial")
    ObjectMapper mapper = new ObjectMapper()
        .registerModule(new ProtobufModule())
        .setPropertyNamingStrategy(snakeCaseNamingBase());

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

  @Test(expected = UnrecognizedPropertyException.class)
  public void itDoesntAcceptUnderscoreNameForCamelcasePropertyByDefault() throws IOException {
    String json = "{\"string_attribute\":\"test\"}";
    camelCase().readValue(json, PropertyNamingSnakeCased.class);
  }

  /**
   * If the protobuf property is underscore, we expect the JSON field name to be camelcase.
   * But if the JSON field name is already underscore, we should still accept it if you enable the feature
   */
  @Test
  public void itAcceptsUnderscoreNameForCamelcasePropertyIfYouEnableIt() throws IOException {
    ProtobufJacksonConfig config = ProtobufJacksonConfig.builder().acceptLiteralFieldnames(true).build();
    ObjectMapper mapper = new ObjectMapper().registerModules(new ProtobufModule(config));

    String json = "{\"string_attribute\":\"test\"}";
    PropertyNamingSnakeCased message = mapper.readValue(json, PropertyNamingSnakeCased.class);

    assertThat(message.getStringAttribute()).isEqualTo("test");
  }

  private static PropertyNamingStrategy snakeCaseNamingBase() {
    try {
      return new NamingBase() {
        @Override
        public String translate(String propertyName) {
          return propertyName;
        }
      };
    } catch (Throwable t) {
      return new PropertyNamingStrategyBase() {
        @Override
        public String translate(String propertyName) {
          return propertyName;
        }
      };
    }
  }
}
