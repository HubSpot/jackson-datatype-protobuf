package com.hubspot.jackson.datatype.protobuf;

import static com.hubspot.jackson.datatype.protobuf.util.ObjectMapperHelper.camelCase;
import static com.hubspot.jackson.datatype.protobuf.util.ObjectMapperHelper.create;
import static org.assertj.core.api.Assertions.assertThat;

import com.hubspot.jackson.datatype.protobuf.util.TestProtobuf;
import com.hubspot.jackson.datatype.protobuf.util.TestProtobuf.AllFields;
import org.junit.Test;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.DatabindException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.EnumFeature;

public class FailOnNumbersForEnumsTest {

  @Test(expected = DatabindException.class)
  public void testEnabled() throws JacksonException {
    ObjectMapper mapper = objectMapper(true);

    mapper.treeToValue(buildNode(), AllFields.class);
  }

  @Test
  public void testDisabled() throws JacksonException {
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
      return create().rebuild().enable(EnumFeature.FAIL_ON_NUMBERS_FOR_ENUMS).build();
    } else {
      return create().rebuild().disable(EnumFeature.FAIL_ON_NUMBERS_FOR_ENUMS).build();
    }
  }
}
