package com.hubspot.jackson.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hubspot.jackson.test.util.TestProtobuf.AllFields;
import org.junit.Test;

import static com.hubspot.jackson.test.util.ObjectMapperHelper.camelCase;
import static org.fest.assertions.api.Assertions.assertThat;

public class FailOnNullPrimitivesTest {

  @Test(expected = JsonMappingException.class)
  public void testIntEnabled() throws JsonProcessingException {
    ObjectNode node = buildNode("int32");

    objectMapper(true).treeToValue(node, AllFields.class);
  }

  @Test
  public void testIntDisabled() throws JsonProcessingException {
    ObjectNode node = buildNode("int32");

    AllFields parsed = objectMapper(false).treeToValue(node, AllFields.class);
    assertThat(parsed.hasInt32()).isFalse();
  }

  @Test(expected = JsonMappingException.class)
  public void testLongEnabled() throws JsonProcessingException {
    ObjectNode node = buildNode("int64");

    objectMapper(true).treeToValue(node, AllFields.class);
  }

  @Test
  public void testLongDisabled() throws JsonProcessingException {
    ObjectNode node = buildNode("int64");

    AllFields parsed = objectMapper(false).treeToValue(node, AllFields.class);
    assertThat(parsed.hasInt64()).isFalse();
  }

  @Test(expected = JsonMappingException.class)
  public void testFloatEnabled() throws JsonProcessingException {
    ObjectNode node = buildNode("float");

    objectMapper(true).treeToValue(node, AllFields.class);
  }

  @Test
  public void testFloatDisabled() throws JsonProcessingException {
    ObjectNode node = buildNode("float");

    AllFields parsed = objectMapper(false).treeToValue(node, AllFields.class);
    assertThat(parsed.hasFloat()).isFalse();
  }

  @Test(expected = JsonMappingException.class)
  public void testDoubleEnabled() throws JsonProcessingException {
    ObjectNode node = buildNode("double");

    objectMapper(true).treeToValue(node, AllFields.class);
  }

  @Test
  public void tesDoubleDisabled() throws JsonProcessingException {
    ObjectNode node = buildNode("double");

    AllFields parsed = objectMapper(false).treeToValue(node, AllFields.class);
    assertThat(parsed.hasDouble()).isFalse();
  }

  @Test(expected = JsonMappingException.class)
  public void testBooleanEnabled() throws JsonProcessingException {
    ObjectNode node = buildNode("bool");

    objectMapper(true).treeToValue(node, AllFields.class);
  }

  @Test
  public void testBooleanDisabled() throws JsonProcessingException {
    ObjectNode node = buildNode("bool");

    AllFields parsed = objectMapper(false).treeToValue(node, AllFields.class);
    assertThat(parsed.hasBool()).isFalse();
  }

  @Test
  public void testOnlyAffectsPrimitives() throws JsonProcessingException {
    ObjectNode node = buildNode("string", "bytes", "enum", "nested");

    AllFields parsed = objectMapper(true).treeToValue(node, AllFields.class);
    assertThat(parsed.hasString()).isFalse();
    assertThat(parsed.hasBytes()).isFalse();
    assertThat(parsed.hasEnum()).isFalse();
    assertThat(parsed.hasNested()).isFalse();
  }

  private ObjectNode buildNode(String... fieldNames) {
    ObjectNode node = camelCase().createObjectNode();

    for (String fieldName : fieldNames) {
      node.putNull(fieldName);
    }

    return node;
  }

  private static ObjectMapper objectMapper(boolean enabled) {
    if (enabled) {
      return camelCase().enable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES);
    } else {
      return camelCase().disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES);
    }
  }
}
