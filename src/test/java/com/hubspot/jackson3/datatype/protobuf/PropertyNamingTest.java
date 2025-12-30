package com.hubspot.jackson3.datatype.protobuf;

import static com.hubspot.jackson3.datatype.protobuf.util.ObjectMapperHelper.camelCase;
import static com.hubspot.jackson3.datatype.protobuf.util.ObjectMapperHelper.create;
import static com.hubspot.jackson3.datatype.protobuf.util.ObjectMapperHelper.toTree;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import com.hubspot.jackson3.datatype.protobuf.util.CompileCustomProtobufs.MixedJsonName;
import com.hubspot.jackson3.datatype.protobuf.util.ObjectMapperHelper;
import com.hubspot.jackson3.datatype.protobuf.util.ProtobufCreator;
import com.hubspot.jackson3.datatype.protobuf.util.TestProtobuf.PropertyNamingCamelCased;
import com.hubspot.jackson3.datatype.protobuf.util.TestProtobuf.PropertyNamingJsonName;
import com.hubspot.jackson3.datatype.protobuf.util.TestProtobuf.PropertyNamingSnakeCased;
import com.hubspot.jackson3.datatype.protobuf.util.TestProtobuf3.JsonNameProto3;
import java.util.List;
import org.junit.Test;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.PropertyNamingStrategy;
import tools.jackson.databind.cfg.MapperConfig;
import tools.jackson.databind.exc.UnrecognizedPropertyException;
import tools.jackson.databind.introspect.AnnotatedField;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.JsonNodeFactory;
import tools.jackson.databind.node.ObjectNode;

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
    assertThat(tree.get("stringAttribute").stringValue())
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
      assertThat(subTree.get("stringAttribute").stringValue())
        .isEqualTo(messages.get(i).getStringAttribute());
    }
  }

  @Test
  public void testSingleUnderscore() {
    PropertyNamingSnakeCased message = ProtobufCreator.create(
      PropertyNamingSnakeCased.class
    );

    JsonNode tree = toTree(ObjectMapperHelper.underscore(), message);

    assertThat(tree.isObject()).isTrue();
    assertThat(tree.size()).isEqualTo(1);
    assertThat(tree.get("string_attribute")).isNotNull();
    assertThat(tree.get("string_attribute").stringValue())
      .isEqualTo(message.getStringAttribute());
  }

  @Test
  public void testMultipleUnderscore() {
    List<PropertyNamingSnakeCased> messages = ProtobufCreator.create(
      PropertyNamingSnakeCased.class,
      10
    );

    JsonNode tree = toTree(ObjectMapperHelper.underscore(), messages);

    assertThat(tree).isInstanceOf(ArrayNode.class);
    assertThat(tree.size()).isEqualTo(10);

    for (int i = 0; i < 10; i++) {
      JsonNode subTree = tree.get(i);

      assertThat(subTree.isObject()).isTrue();
      assertThat(subTree.size()).isEqualTo(1);
      assertThat(subTree.get("string_attribute")).isNotNull();
      assertThat(subTree.get("string_attribute").stringValue())
        .isEqualTo(messages.get(i).getStringAttribute());
    }

    tree = toTree(ObjectMapperHelper.underscore(), messages);

    assertThat(tree).isInstanceOf(ArrayNode.class);
    assertThat(tree.size()).isEqualTo(10);

    for (int i = 0; i < 10; i++) {
      JsonNode subTree = tree.get(i);

      assertThat(subTree.isObject()).isTrue();
      assertThat(subTree.size()).isEqualTo(1);
      assertThat(subTree.get("string_attribute")).isNotNull();
      assertThat(subTree.get("string_attribute").stringValue())
        .isEqualTo(messages.get(i).getStringAttribute());
    }
  }

  @Test
  public void testSingleStillCamelCase() {
    PropertyNamingCamelCased message = ProtobufCreator.create(
      PropertyNamingCamelCased.class
    );

    @SuppressWarnings("serial")
    ObjectMapper mapper = create()
      .propertyNamingStrategy(
        new PropertyNamingStrategy() {
          @Override
          public String nameForField(
            MapperConfig<?> config,
            AnnotatedField field,
            String defaultName
          ) {
            return defaultName;
          }
        }
      )
      .build();

    JsonNode tree = toTree(mapper, message);

    assertThat(tree.isObject()).isTrue();
    assertThat(tree.size()).isEqualTo(1);
    assertThat(tree.get("stringattribute")).isNotNull();
    assertThat(tree.get("stringattribute").stringValue())
      .isEqualTo(message.getStringAttribute());
  }

  @Test
  public void testSingleNoNamingStrategy() {
    PropertyNamingCamelCased message = ProtobufCreator.create(
      PropertyNamingCamelCased.class
    );

    @SuppressWarnings("serial")
    ObjectMapper mapper = create().build();

    JsonNode tree = toTree(mapper, message);

    assertThat(tree.isObject()).isTrue();
    assertThat(tree.size()).isEqualTo(1);
    assertThat(tree.get("stringattribute")).isNotNull();
    assertThat(tree.get("stringattribute").stringValue())
      .isEqualTo(message.getStringAttribute());
  }

  @Test
  public void testSingleStillCamelCaseUsingNamingBase() {
    PropertyNamingCamelCased message = ProtobufCreator.create(
      PropertyNamingCamelCased.class
    );

    @SuppressWarnings("serial")
    ObjectMapper mapper = create().propertyNamingStrategy(snakeCaseNamingBase()).build();

    JsonNode tree = toTree(mapper, message);

    assertThat(tree.isObject()).isTrue();
    assertThat(tree.size()).isEqualTo(1);
    assertThat(tree.get("stringAttribute")).isNotNull();
    assertThat(tree.get("stringAttribute").stringValue())
      .isEqualTo(message.getStringAttribute());
  }

  @Test
  public void testMultipleStillCamelCase() {
    List<PropertyNamingCamelCased> messages = ProtobufCreator.create(
      PropertyNamingCamelCased.class,
      10
    );

    @SuppressWarnings("serial")
    ObjectMapper mapper = create()
      .propertyNamingStrategy(
        new PropertyNamingStrategy() {
          @Override
          public String nameForField(
            MapperConfig<?> config,
            AnnotatedField field,
            String defaultName
          ) {
            return defaultName;
          }
        }
      )
      .build();

    JsonNode tree = toTree(mapper, messages);

    assertThat(tree).isInstanceOf(ArrayNode.class);
    assertThat(tree.size()).isEqualTo(10);

    for (int i = 0; i < 10; i++) {
      JsonNode subTree = tree.get(i);

      assertThat(subTree.isObject()).isTrue();
      assertThat(subTree.size()).isEqualTo(1);
      assertThat(subTree.get("stringattribute")).isNotNull();
      assertThat(subTree.get("stringattribute").stringValue())
        .isEqualTo(messages.get(i).getStringAttribute());
    }
  }

  @Test
  public void testMultipleNoNamingStrategy() {
    List<PropertyNamingCamelCased> messages = ProtobufCreator.create(
      PropertyNamingCamelCased.class,
      10
    );

    @SuppressWarnings("serial")
    ObjectMapper mapper = create().build();

    JsonNode tree = toTree(mapper, messages);

    assertThat(tree).isInstanceOf(ArrayNode.class);
    assertThat(tree.size()).isEqualTo(10);

    for (int i = 0; i < 10; i++) {
      JsonNode subTree = tree.get(i);

      assertThat(subTree.isObject()).isTrue();
      assertThat(subTree.size()).isEqualTo(1);
      assertThat(subTree.get("stringattribute")).isNotNull();
      assertThat(subTree.get("stringattribute").stringValue())
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
    ObjectMapper mapper = create().propertyNamingStrategy(snakeCaseNamingBase()).build();

    JsonNode tree = toTree(mapper, messages);

    assertThat(tree).isInstanceOf(ArrayNode.class);
    assertThat(tree.size()).isEqualTo(10);

    for (int i = 0; i < 10; i++) {
      JsonNode subTree = tree.get(i);

      assertThat(subTree.isObject()).isTrue();
      assertThat(subTree.size()).isEqualTo(1);
      assertThat(subTree.get("stringAttribute")).isNotNull();
      assertThat(subTree.get("stringAttribute").stringValue())
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
  public void itAcceptsUnderscoreNameForCamelcasePropertyIfYouEnableIt() {
    ProtobufJacksonConfig config = ProtobufJacksonConfig
      .builder()
      .acceptLiteralFieldnames(true)
      .build();
    ObjectMapper mapper = create(config).build();

    String json = "{\"string_attribute\":\"test\"}";
    PropertyNamingSnakeCased message = mapper.readValue(
      json,
      PropertyNamingSnakeCased.class
    );

    assertThat(message.getStringAttribute()).isEqualTo("test");
  }

  @Test
  public void itRespectsJsonNameAttributeProto2() {
    ObjectMapper mapper = create().build();
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
  public void itAcceptsLiteralNameForMessageWithJsonNameAttributeProto2() {
    ObjectMapper mapper = create(
      ProtobufJacksonConfig.builder().acceptLiteralFieldnames(true).build()
    )
      .build();
    String json =
      "{\"custom_name\":\"v\",\"lower_camel\":\"v2\",\"lower_underscore\":\"v3\",\"different_name\":\"v4\"}";
    PropertyNamingJsonName message = mapper.readValue(json, PropertyNamingJsonName.class);

    assertThat(message.getCustomName()).isEqualTo("v");
    assertThat(message.getLowerCamel()).isEqualTo("v2");
    assertThat(message.getLowerUnderscore()).isEqualTo("v3");
    assertThat(message.getDifferentName()).isEqualTo("v4");
  }

  @Test
  public void itRespectsJsonNameAttributeProto3() {
    ObjectMapper mapper = create().build();
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
  public void itAcceptsLiteralNameForMessageWithJsonNameAttributeProto3() {
    ObjectMapper mapper = create(
      ProtobufJacksonConfig.builder().acceptLiteralFieldnames(true).build()
    )
      .build();
    String json =
      "{\"custom_name\":\"v\",\"lower_camel\":\"v2\",\"lower_underscore\":\"v3\",\"different_name\":\"v4\"}";
    JsonNameProto3 message = mapper.readValue(json, JsonNameProto3.class);

    assertThat(message.getCustomName()).isEqualTo("v");
    assertThat(message.getLowerCamel()).isEqualTo("v2");
    assertThat(message.getLowerUnderscore()).isEqualTo("v3");
    assertThat(message.getDifferentName()).isEqualTo("v4");
  }

  @Test
  public void itHandlesProtosCompiledFromDescriptorSet() {
    // protos compiled from descriptor set always have json_name populated
    // https://github.com/protocolbuffers/protobuf/issues/6175

    ObjectMapper mapper = create(
      ProtobufJacksonConfig.builder().acceptLiteralFieldnames(true).build()
    )
      .propertyNamingStrategy(
        new PropertyNamingStrategies.NamingBase() {
          @Override
          public String translate(String propertyName) {
            return propertyName.toUpperCase();
          }
        }
      )
      .build();

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

  @Test
  public void ensureSerializationBehavior() {
    ObjectMapper original = create().build();
    ObjectMapper custom = create()
      .propertyNamingStrategy(new PropertyNamingStrategy() {})
      .build();

    PropertyNamingSnakeCased snakeCase = PropertyNamingSnakeCased
      .newBuilder()
      .setStringAttribute("value")
      .build();
    PropertyNamingCamelCased camelCase = PropertyNamingCamelCased
      .newBuilder()
      .setStringAttribute("value")
      .build();

    assertThat(original.<JsonNode>valueToTree(snakeCase))
      .isEqualTo(objectNode("stringAttribute", "value"));
    assertThat(custom.<JsonNode>valueToTree(snakeCase))
      .isEqualTo(objectNode("stringAttribute", "value"));

    assertThat(original.<JsonNode>valueToTree(camelCase))
      .isEqualTo(objectNode("stringattribute", "value"));
    assertThat(custom.<JsonNode>valueToTree(camelCase))
      .isEqualTo(objectNode("stringattribute", "value"));
  }

  @Test
  public void ensureDeserializationBehavior() {
    ObjectMapper original = create().build();
    ObjectMapper custom = create()
      .propertyNamingStrategy(new PropertyNamingStrategy() {})
      .build();

    PropertyNamingSnakeCased snakeCase = PropertyNamingSnakeCased
      .newBuilder()
      .setStringAttribute("value")
      .build();
    PropertyNamingCamelCased camelCase = PropertyNamingCamelCased
      .newBuilder()
      .setStringAttribute("value")
      .build();

    assertThat(
      original.treeToValue(
        objectNode("stringAttribute", "value"),
        PropertyNamingSnakeCased.class
      )
    )
      .isEqualTo(snakeCase);

    assertThat(
      custom.treeToValue(
        objectNode("stringAttribute", "value"),
        PropertyNamingSnakeCased.class
      )
    )
      .isEqualTo(snakeCase);

    assertThat(
      original.treeToValue(
        objectNode("stringattribute", "value"),
        PropertyNamingCamelCased.class
      )
    )
      .isEqualTo(camelCase);

    assertThat(
      custom.treeToValue(
        objectNode("stringattribute", "value"),
        PropertyNamingCamelCased.class
      )
    )
      .isEqualTo(camelCase);
  }

  private static PropertyNamingStrategy snakeCaseNamingBase() {
    return new PropertyNamingStrategies.NamingBase() {
      @Override
      public String translate(String propertyName) {
        return propertyName;
      }
    };
  }

  private static ObjectNode objectNode(String field, String value) {
    return JsonNodeFactory.instance.objectNode().put(field, value);
  }
}
