package com.hubspot.jackson.datatype.protobuf;

import static com.hubspot.jackson.datatype.protobuf.util.ObjectMapperHelper.camelCase;
import static com.hubspot.jackson.datatype.protobuf.util.ObjectMapperHelper.create;
import static org.assertj.core.api.Assertions.assertThat;

import com.hubspot.jackson.datatype.protobuf.util.TestProtobuf.AllFields;
import org.junit.Test;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

public class FailOnNullPrimitivesTest {

  @Test(expected = JacksonException.class)
  public void testIntEnabled() {
    ObjectNode node = buildNode("int32");

    objectMapper(true).treeToValue(node, AllFields.class);
  }

  @Test
  public void testIntDisabled() {
    ObjectNode node = buildNode("int32");

    AllFields parsed = objectMapper(false).treeToValue(node, AllFields.class);
    assertThat(parsed.hasInt32()).isFalse();
  }

  @Test(expected = JacksonException.class)
  public void testLongEnabled() {
    ObjectNode node = buildNode("int64");

    objectMapper(true).treeToValue(node, AllFields.class);
  }

  @Test
  public void testLongDisabled() {
    ObjectNode node = buildNode("int64");

    AllFields parsed = objectMapper(false).treeToValue(node, AllFields.class);
    assertThat(parsed.hasInt64()).isFalse();
  }

  @Test(expected = JacksonException.class)
  public void testFloatEnabled() {
    ObjectNode node = buildNode("float");

    objectMapper(true).treeToValue(node, AllFields.class);
  }

  @Test
  public void testFloatDisabled() {
    ObjectNode node = buildNode("float");

    AllFields parsed = objectMapper(false).treeToValue(node, AllFields.class);
    assertThat(parsed.hasFloat()).isFalse();
  }

  @Test(expected = JacksonException.class)
  public void testDoubleEnabled() {
    ObjectNode node = buildNode("double");

    objectMapper(true).treeToValue(node, AllFields.class);
  }

  @Test
  public void tesDoubleDisabled() {
    ObjectNode node = buildNode("double");

    AllFields parsed = objectMapper(false).treeToValue(node, AllFields.class);
    assertThat(parsed.hasDouble()).isFalse();
  }

  @Test(expected = JacksonException.class)
  public void testBooleanEnabled() {
    ObjectNode node = buildNode("bool");

    objectMapper(true).treeToValue(node, AllFields.class);
  }

  @Test
  public void testBooleanDisabled() {
    ObjectNode node = buildNode("bool");

    AllFields parsed = objectMapper(false).treeToValue(node, AllFields.class);
    assertThat(parsed.hasBool()).isFalse();
  }

  @Test
  public void testOnlyAffectsPrimitives() {
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
      return create().enable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES).build();
    } else {
      return create().disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES).build();
    }
  }
}
