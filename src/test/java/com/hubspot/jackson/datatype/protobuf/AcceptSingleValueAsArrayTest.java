package com.hubspot.jackson.datatype.protobuf;

import static com.hubspot.jackson.datatype.protobuf.util.ObjectMapperHelper.camelCase;
import static com.hubspot.jackson.datatype.protobuf.util.ObjectMapperHelper.create;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hubspot.jackson.datatype.protobuf.util.TestProtobuf.RepeatedFields;
import org.junit.Test;

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
      return create().enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
    } else {
      return create().disable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
    }
  }
}
