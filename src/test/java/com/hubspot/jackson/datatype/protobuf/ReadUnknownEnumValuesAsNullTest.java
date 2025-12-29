package com.hubspot.jackson.datatype.protobuf;

import static com.hubspot.jackson.datatype.protobuf.util.ObjectMapperHelper.camelCase;
import static com.hubspot.jackson.datatype.protobuf.util.ObjectMapperHelper.create;
import static org.assertj.core.api.Assertions.assertThat;

import com.hubspot.jackson.datatype.protobuf.util.TestProtobuf.AllFields;
import org.junit.Test;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.EnumFeature;

public class ReadUnknownEnumValuesAsNullTest {

  @Test
  public void testStringEnabled() {
    ObjectMapper mapper = objectMapper(true);

    AllFields parsed = mapper.treeToValue(buildNode("fakeValue"), AllFields.class);
    assertThat(parsed.hasEnum()).isFalse();
  }

  @Test(expected = JacksonException.class)
  public void testStringDisabled() {
    ObjectMapper mapper = objectMapper(false);

    mapper.treeToValue(buildNode("fakeValue"), AllFields.class);
  }

  @Test
  public void testIntEnabled() {
    ObjectMapper mapper = objectMapper(true);

    AllFields parsed = mapper.treeToValue(buildNode(999999), AllFields.class);
    assertThat(parsed.hasEnum()).isFalse();
  }

  @Test(expected = JacksonException.class)
  public void testIntDisabled() {
    ObjectMapper mapper = objectMapper(false);

    mapper.treeToValue(buildNode(999999), AllFields.class);
  }

  private static JsonNode buildNode(String value) {
    return camelCase().createObjectNode().put("enum", value);
  }

  private static JsonNode buildNode(int value) {
    return camelCase().createObjectNode().put("enum", value);
  }

  private static ObjectMapper objectMapper(boolean enabled) {
    if (enabled) {
      return create().enable(EnumFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL).build();
    } else {
      return create().disable(EnumFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL).build();
    }
  }
}
