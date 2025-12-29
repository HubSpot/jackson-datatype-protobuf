package com.hubspot.jackson.datatype.protobuf;

import static com.hubspot.jackson.datatype.protobuf.util.ObjectMapperHelper.camelCase;
import static com.hubspot.jackson.datatype.protobuf.util.ObjectMapperHelper.create;
import static org.assertj.core.api.Assertions.assertThat;

import com.hubspot.jackson.datatype.protobuf.util.TestProtobuf.RepeatedFields;
import org.junit.Test;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

public class AcceptSingleValueAsArrayTest {

  @Test
  public void testEnabled() {
    ObjectMapper mapper = objectMapper(true);

    RepeatedFields parsed = mapper.treeToValue(buildNode(), RepeatedFields.class);
    assertThat(parsed.getBoolCount()).isEqualTo(1);
    assertThat(parsed.getBoolList()).containsExactly(true);
  }

  @Test(expected = JacksonException.class)
  public void testDisabled() {
    ObjectMapper mapper = objectMapper(false);

    mapper.treeToValue(buildNode(), RepeatedFields.class);
  }

  private static JsonNode buildNode() {
    return camelCase().createObjectNode().put("bool", true);
  }

  private static ObjectMapper objectMapper(boolean enabled) {
    if (enabled) {
      return create().enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY).build();
    } else {
      return create()
        .disable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        .build();
    }
  }
}
