package com.hubspot.jackson.datatype.protobuf;

import static com.hubspot.jackson.datatype.protobuf.util.ObjectMapperHelper.camelCase;
import static com.hubspot.jackson.datatype.protobuf.util.ObjectMapperHelper.create;
import static org.assertj.core.api.Assertions.assertThat;

import com.hubspot.jackson.datatype.protobuf.util.TestProtobuf.AllFields;
import org.junit.Test;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.DatabindException;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

public class FailOnNullPrimitivesTest {

  @Test(expected = DatabindException.class)
  public void testIntEnabled() throws JacksonException {
    ObjectNode node = buildNode("int32");

    objectMapper(true).treeToValue(node, AllFields.class);
  }

  @Test
  public void testIntDisabled() throws JacksonException {
    ObjectNode node = buildNode("int32");

    AllFields parsed = objectMapper(false).treeToValue(node, AllFields.class);
    assertThat(parsed.hasInt32()).isFalse();
  }

  @Test(expected = DatabindException.class)
  public void testLongEnabled() throws JacksonException {
    ObjectNode node = buildNode("int64");

    objectMapper(true).treeToValue(node, AllFields.class);
  }

  @Test
  public void testLongDisabled() throws JacksonException {
    ObjectNode node = buildNode("int64");

    AllFields parsed = objectMapper(false).treeToValue(node, AllFields.class);
    assertThat(parsed.hasInt64()).isFalse();
  }

  @Test(expected = DatabindException.class)
  public void testFloatEnabled() throws JacksonException {
    ObjectNode node = buildNode("float");

    objectMapper(true).treeToValue(node, AllFields.class);
  }

  @Test
  public void testFloatDisabled() throws JacksonException {
    ObjectNode node = buildNode("float");

    AllFields parsed = objectMapper(false).treeToValue(node, AllFields.class);
    assertThat(parsed.hasFloat()).isFalse();
  }

  @Test(expected = DatabindException.class)
  public void testDoubleEnabled() throws JacksonException {
    ObjectNode node = buildNode("double");

    objectMapper(true).treeToValue(node, AllFields.class);
  }

  @Test
  public void tesDoubleDisabled() throws JacksonException {
    ObjectNode node = buildNode("double");

    AllFields parsed = objectMapper(false).treeToValue(node, AllFields.class);
    assertThat(parsed.hasDouble()).isFalse();
  }

  @Test(expected = DatabindException.class)
  public void testBooleanEnabled() throws JacksonException {
    ObjectNode node = buildNode("bool");

    objectMapper(true).treeToValue(node, AllFields.class);
  }

  @Test
  public void testBooleanDisabled() throws JacksonException {
    ObjectNode node = buildNode("bool");

    AllFields parsed = objectMapper(false).treeToValue(node, AllFields.class);
    assertThat(parsed.hasBool()).isFalse();
  }

  @Test
  public void testOnlyAffectsPrimitives() throws JacksonException {
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
      return create()
        .rebuild()
        .enable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
        .build();
    } else {
      return create()
        .rebuild()
        .disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
        .build();
    }
  }
}
