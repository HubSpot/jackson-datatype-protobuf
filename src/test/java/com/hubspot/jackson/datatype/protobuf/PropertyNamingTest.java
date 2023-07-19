package com.hubspot.jackson.datatype.protobuf;

import static com.hubspot.jackson.datatype.protobuf.util.ObjectMapperHelper.camelCase;
import static com.hubspot.jackson.datatype.protobuf.util.ObjectMapperHelper.toTree;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.NamingBase;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.PropertyNamingStrategyBase;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hubspot.jackson.datatype.protobuf.util.CompileCustomProtobufs.MixedJsonName;
import com.hubspot.jackson.datatype.protobuf.util.ObjectMapperHelper;
import com.hubspot.jackson.datatype.protobuf.util.ProtobufCreator;
import com.hubspot.jackson.datatype.protobuf.util.TestProtobuf.PropertyNamingCamelCased;
import com.hubspot.jackson.datatype.protobuf.util.TestProtobuf.PropertyNamingJsonName;
import com.hubspot.jackson.datatype.protobuf.util.TestProtobuf.PropertyNamingSnakeCased;
import com.hubspot.jackson.datatype.protobuf.util.TestProtobuf3.JsonNameProto3;
import java.io.IOException;
import java.util.List;
import org.junit.Test;

public class PropertyNamingTest {

  @Test
  public void testSingleSnakeCaseToCamelCase() {
    PropertyNamingSnakeCased message = ProtobufCreator.create(
      PropertyNamingSnakeCased.class
    );

    JsonNode tree = toTree(camelCase(), message);

    assertThat(tree.isObject()).isTrue();
    assertThat(tree.size()).isEqualTo(1);
    assertThat(tree.get("stringAttribute")).isNotNull();
    assertThat(tree.get("stringAttribute").textValue())
      .isEqualTo(message.getStringAttribute());
  }

  @Test
  public void testMultipleSnakeCaseToCamelCase() {
    List<PropertyNamingSnakeCased> messages = ProtobufCreator.create(
      PropertyNamingSnakeCased.class,
      10
    );

    JsonNode tree = toTree(camelCase(), messages);

    assertThat(tree).isInstanceOf(ArrayNode.class);
    assertThat(tree.size()).isEqualTo(10);

    for (int i = 0; i < 10; i++) {
      JsonNode subTree = tree.get(i);

      assertThat(subTree.isObject()).isTrue();
      assertThat(subTree.size()).isEqualTo(1);
      assertThat(subTree.get("stringAttribute")).isNotNull();
      assertThat(subTree.get("stringAttribute").textValue())
        .isEqualTo(messages.get(i).getStringAttribute());
    }
  }

  @Test
  public void testSingleUnderscore() {
    PropertyNamingSnakeCased message = ProtobufCreator.create(
      PropertyNamingSnakeCased.class
    );

    JsonNode tree = toTree(ObjectMapperHelper.oldUnderscore(), message);

    assertThat(tree.isObject()).isTrue();
    assertThat(tree.size()).isEqualTo(1);
    assertThat(tree.get("string_attribute")).isNotNull();
    assertThat(tree.get("string_attribute").textValue())
      .isEqualTo(message.getStringAttribute());

    tree = toTree(ObjectMapperHelper.newUnderscore(), message);

    assertThat(tree.isObject()).isTrue();
    assertThat(tree.size()).isEqualTo(1);
    assertThat(tree.get("string_attribute")).isNotNull();
    assertThat(tree.get("string_attribute").textValue())
      .isEqualTo(message.getStringAttribute());
  }

  @Test
  public void testMultipleUnderscore() {
    List<PropertyNamingSnakeCased> messages = ProtobufCreator.create(
      PropertyNamingSnakeCased.class,
      10
    );

    JsonNode tree = toTree(ObjectMapperHelper.oldUnderscore(), messages);

    assertThat(tree).isInstanceOf(ArrayNode.class);
    assertThat(tree.size()).isEqualTo(10);

    for (int i = 0; i < 10; i++) {
      JsonNode subTree = tree.get(i);

      assertThat(subTree.isObject()).isTrue();
      assertThat(subTree.size()).isEqualTo(1);
      assertThat(subTree.get("string_attribute")).isNotNull();
      assertThat(subTree.get("string_attribute").textValue())
        .isEqualTo(messages.get(i).getStringAttribute());
    }

    tree = toTree(ObjectMapperHelper.newUnderscore(), messages);

    assertThat(tree).isInstanceOf(ArrayNode.class);
    assertThat(tree.size()).isEqualTo(10);

    for (int i = 0; i < 10; i++) {
      JsonNode subTree = tree.get(i);

      assertThat(subTree.isObject()).isTrue();
      assertThat(subTree.size()).isEqualTo(1);
      assertThat(subTree.get("string_attribute")).isNotNull();
      assertThat(subTree.get("string_attribute").textValue())
        .isEqualTo(messages.get(i).getStringAttribute());
    }
  }

  @Test
  public void testSingleStillCamelCase() {
    PropertyNamingCamelCased message = ProtobufCreator.create(
      PropertyNamingCamelCased.class
    );

    @SuppressWarnings("serial")
    ObjectMapper mapper = new ObjectMapper()
      .registerModule(new ProtobufModule())
      .setPropertyNamingStrategy(
        new PropertyNamingStrategy.PropertyNamingStrategyBase() {
          @Override
          public String translate(String propertyName) {
            return propertyName;
          }
        }
      );

    JsonNode tree = toTree(mapper, message);

    assertThat(tree.isObject()).isTrue();
    assertThat(tree.size()).isEqualTo(1);
    assertThat(tree.get("stringAttribute")).isNotNull();
    assertThat(tree.get("stringAttribute").textValue())
      .isEqualTo(message.getStringAttribute());
  }

  @Test
  public void testSingleStillCamelCaseUsingNamingBase() {
    PropertyNamingCamelCased message = ProtobufCreator.create(
      PropertyNamingCamelCased.class
    );

    @SuppressWarnings("serial")
    ObjectMapper mapper = new ObjectMapper()
      .registerModule(new ProtobufModule())
      .setPropertyNamingStrategy(snakeCaseNamingBase());

    JsonNode tree = toTree(mapper, message);

    assertThat(tree.isObject()).isTrue();
    assertThat(tree.size()).isEqualTo(1);
    assertThat(tree.get("stringAttribute")).isNotNull();
    assertThat(tree.get("stringAttribute").textValue())
      .isEqualTo(message.getStringAttribute());
  }

  @Test
  public void testMultipleStillCamelCase() {
    List<PropertyNamingCamelCased> messages = ProtobufCreator.create(
      PropertyNamingCamelCased.class,
      10
    );

    @SuppressWarnings("serial")
    ObjectMapper mapper = new ObjectMapper()
      .registerModule(new ProtobufModule())
      .setPropertyNamingStrategy(
        new PropertyNamingStrategy.PropertyNamingStrategyBase() {
          @Override
          public String translate(String propertyName) {
            return propertyName;
          }
        }
      );

    JsonNode tree = toTree(mapper, messages);

    assertThat(tree).isInstanceOf(ArrayNode.class);
    assertThat(tree.size()).isEqualTo(10);

    for (int i = 0; i < 10; i++) {
      JsonNode subTree = tree.get(i);

      assertThat(subTree.isObject()).isTrue();
      assertThat(subTree.size()).isEqualTo(1);
      assertThat(subTree.get("stringAttribute")).isNotNull();
      assertThat(subTree.get("stringAttribute").textValue())
        .isEqualTo(messages.get(i).getStringAttribute());
    }
  }

  @Test
  public void testMultipleStillCamelCaseUsingNamingBase() {
    List<PropertyNamingCamelCased> messages = ProtobufCreator.create(
      PropertyNamingCamelCased.class,
      10
    );

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
      assertThat(subTree.get("stringAttribute").textValue())
        .isEqualTo(messages.get(i).getStringAttribute());
    }
  }

  @Test
  public void itDoesntAcceptUnderscoreNameForCamelcasePropertyByDefault() {
    String json = "{\"string_attribute\":\"test\"}";

    Throwable t = catchThrowable(() ->
      camelCase().readValue(json, PropertyNamingSnakeCased.class)
    );
    assertThat(t).isInstanceOf(UnrecognizedPropertyException.class);
  }

  /**
   * If the protobuf property is underscore, we expect the JSON field name to be camelcase.
   * But if the JSON field name is already underscore, we should still accept it if you enable the feature
   */
  @Test
  public void itAcceptsUnderscoreNameForCamelcasePropertyIfYouEnableIt()
    throws IOException {
    ProtobufJacksonConfig config = ProtobufJacksonConfig
      .builder()
      .acceptLiteralFieldnames(true)
      .build();
    ObjectMapper mapper = new ObjectMapper().registerModules(new ProtobufModule(config));

    String json = "{\"string_attribute\":\"test\"}";
    PropertyNamingSnakeCased message = mapper.readValue(
      json,
      PropertyNamingSnakeCased.class
    );

    assertThat(message.getStringAttribute()).isEqualTo("test");
  }

  @Test
  public void itRespectsJsonNameAttributeProto2() throws IOException {
    ObjectMapper mapper = new ObjectMapper().registerModules(new ProtobufModule());
    String json =
      "{\"custom-name\":\"v\",\"lowerCamel\":\"v2\",\"lower_underscore\":\"v3\",\"surprise!\":\"v4\"}";
    PropertyNamingJsonName message = mapper.readValue(json, PropertyNamingJsonName.class);

    assertThat(message.getCustomName()).isEqualTo("v");
    assertThat(message.getLowerCamel()).isEqualTo("v2");
    assertThat(message.getLowerUnderscore()).isEqualTo("v3");
    assertThat(message.getDifferentName()).isEqualTo("v4");
    assertThat(mapper.writeValueAsString(message)).isEqualTo(json);
  }

  @Test
  public void itAcceptsLiteralNameForMessageWithJsonNameAttributeProto2()
    throws IOException {
    ObjectMapper mapper = new ObjectMapper()
      .registerModules(
        new ProtobufModule(
          ProtobufJacksonConfig.builder().acceptLiteralFieldnames(true).build()
        )
      );
    String json =
      "{\"custom_name\":\"v\",\"lower_camel\":\"v2\",\"lower_underscore\":\"v3\",\"different_name\":\"v4\"}";
    PropertyNamingJsonName message = mapper.readValue(json, PropertyNamingJsonName.class);

    assertThat(message.getCustomName()).isEqualTo("v");
    assertThat(message.getLowerCamel()).isEqualTo("v2");
    assertThat(message.getLowerUnderscore()).isEqualTo("v3");
    assertThat(message.getDifferentName()).isEqualTo("v4");
  }

  @Test
  public void itRespectsJsonNameAttributeProto3() throws IOException {
    ObjectMapper mapper = new ObjectMapper().registerModules(new ProtobufModule());
    String json =
      "{\"custom-name\":\"v\",\"lowerCamel\":\"v2\",\"lower_underscore\":\"v3\",\"surprise!\":\"v4\"}";
    JsonNameProto3 message = mapper.readValue(json, JsonNameProto3.class);

    assertThat(message.getCustomName()).isEqualTo("v");
    assertThat(message.getLowerCamel()).isEqualTo("v2");
    assertThat(message.getLowerUnderscore()).isEqualTo("v3");
    assertThat(message.getDifferentName()).isEqualTo("v4");
    assertThat(mapper.writeValueAsString(message)).isEqualTo(json);
  }

  @Test
  public void itAcceptsLiteralNameForMessageWithJsonNameAttributeProto3()
    throws IOException {
    ObjectMapper mapper = new ObjectMapper()
      .registerModules(
        new ProtobufModule(
          ProtobufJacksonConfig.builder().acceptLiteralFieldnames(true).build()
        )
      );
    String json =
      "{\"custom_name\":\"v\",\"lower_camel\":\"v2\",\"lower_underscore\":\"v3\",\"different_name\":\"v4\"}";
    JsonNameProto3 message = mapper.readValue(json, JsonNameProto3.class);

    assertThat(message.getCustomName()).isEqualTo("v");
    assertThat(message.getLowerCamel()).isEqualTo("v2");
    assertThat(message.getLowerUnderscore()).isEqualTo("v3");
    assertThat(message.getDifferentName()).isEqualTo("v4");
  }

  @Test
  public void itHandlesProtosCompiledFromDescriptorSet() throws IOException {
    // protos compiled from descriptor set always have json_name populated
    // https://github.com/protocolbuffers/protobuf/issues/6175

    ObjectMapper mapper = new ObjectMapper()
      .registerModules(
        new ProtobufModule(
          ProtobufJacksonConfig.builder().acceptLiteralFieldnames(true).build()
        )
      )
      .setPropertyNamingStrategy(
        new NamingBase() {
          @Override
          public String translate(String propertyName) {
            return propertyName.toUpperCase();
          }
        }
      );

    MixedJsonName expected = MixedJsonName
      .newBuilder()
      .setFieldWithNoJsonName(123)
      .setFieldWithJsonName(456)
      .build();

    ObjectNode node = mapper
      .createObjectNode()
      .put("field_with_no_json_name", 123)
      .put("field_with_json_name", 456);

    MixedJsonName parsed = mapper.treeToValue(node, MixedJsonName.class);
    assertThat(parsed).isEqualTo(expected);

    node =
      mapper
        .createObjectNode()
        .put("FIELD_WITH_NO_JSON_NAME", 123)
        .put("custom-name", 456);

    parsed = mapper.treeToValue(node, MixedJsonName.class);
    assertThat(parsed).isEqualTo(expected);
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
