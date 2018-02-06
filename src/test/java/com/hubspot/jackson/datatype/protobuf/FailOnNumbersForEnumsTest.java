package com.hubspot.jackson.datatype.protobuf;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hubspot.jackson.datatype.protobuf.util.TestProtobuf;
import com.hubspot.jackson.datatype.protobuf.util.TestProtobuf.AllFields;
import org.junit.Test;

import static com.hubspot.jackson.datatype.protobuf.util.ObjectMapperHelper.camelCase;
import static org.assertj.core.api.Assertions.assertThat;

public class FailOnNumbersForEnumsTest {

  @Test(expected = JsonMappingException.class)
  public void testEnabled() throws JsonProcessingException {
    ObjectMapper mapper = objectMapper(true);

    mapper.treeToValue(buildNode(), AllFields.class);
  }

  @Test
  public void testDisabled() throws JsonProcessingException {
    ObjectMapper mapper = objectMapper(false);

    AllFields parsed = mapper.treeToValue(buildNode(), AllFields.class);
    assertThat(parsed.hasEnum()).isTrue();
    assertThat(parsed.getEnum()).isEqualTo(TestProtobuf.Enum.TWO);
  }

  private static JsonNode buildNode() {
    return camelCase().createObjectNode().put("enum", 2);
  }

  private static ObjectMapper objectMapper(boolean enabled) {
    if (enabled) {
      return camelCase().enable(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS);
    } else {
      return camelCase().disable(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS);
    }
  }
}
