package com.hubspot.jackson.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hubspot.jackson.test.util.TestProtobuf;
import com.hubspot.jackson.test.util.TestProtobuf.AllFields;
import com.hubspot.jackson.test.util.TestProtobuf.RepeatedFields;
import org.junit.Test;

import static com.hubspot.jackson.test.util.ObjectMapperHelper.camelCase;
import static org.assertj.core.api.Assertions.assertThat;

public class AcceptSingleValueAsArrayTest {

  @Test
  public void testEnabled() throws JsonProcessingException {
    ObjectMapper mapper = objectMapper(true);

    RepeatedFields parsed = mapper.treeToValue(buildNode(), RepeatedFields.class);
    assertThat(parsed.getBoolCount()).isEqualTo(1);
    assertThat(parsed.getBoolList()).containsExactly(true);
  }

  @Test(expected = JsonMappingException.class)
  public void testDisabled() throws JsonProcessingException {
    ObjectMapper mapper = objectMapper(false);

    mapper.treeToValue(buildNode(), RepeatedFields.class);
  }

  private static JsonNode buildNode() {
    return camelCase().createObjectNode().put("bool", true);
  }

  private static ObjectMapper objectMapper(boolean enabled) {
    if (enabled) {
      return camelCase().enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
    } else {
      return camelCase().disable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
    }
  }
}
