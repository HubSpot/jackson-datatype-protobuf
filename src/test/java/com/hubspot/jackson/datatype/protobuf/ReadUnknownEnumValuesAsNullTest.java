package com.hubspot.jackson.datatype.protobuf;

import static com.hubspot.jackson.datatype.protobuf.util.ObjectMapperHelper.camelCase;
import static com.hubspot.jackson.datatype.protobuf.util.ObjectMapperHelper.create;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hubspot.jackson.datatype.protobuf.util.TestProtobuf.AllFields;
import org.junit.Test;

public class ReadUnknownEnumValuesAsNullTest {

  @Test
  public void testStringEnabled() throws JsonProcessingException {
    ObjectMapper mapper = objectMapper(true);

    AllFields parsed = mapper.treeToValue(buildNode("fakeValue"), AllFields.class);
    assertThat(parsed.hasEnum()).isFalse();
  }

  @Test(expected = JsonMappingException.class)
  public void testStringDisabled() throws JsonProcessingException {
    ObjectMapper mapper = objectMapper(false);

    mapper.treeToValue(buildNode("fakeValue"), AllFields.class);
  }

  @Test
  public void testIntEnabled() throws JsonProcessingException {
    ObjectMapper mapper = objectMapper(true);

    AllFields parsed = mapper.treeToValue(buildNode(999999), AllFields.class);
    assertThat(parsed.hasEnum()).isFalse();
  }

  @Test(expected = JsonMappingException.class)
  public void testIntDisabled() throws JsonProcessingException {
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
      return create().enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL);
    } else {
      return create().disable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL);
    }
  }
}
