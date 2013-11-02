package com.hubspot.jackson.test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.hubspot.jackson.test.util.TestProtobuf;
import com.hubspot.jackson.test.util.TestProtobuf.AllFields;
import org.junit.Test;

import static com.hubspot.jackson.test.util.ObjectMapperHelper.camelCase;
import static com.hubspot.jackson.test.util.ObjectMapperHelper.writeAndReadBack;
import static org.fest.assertions.api.Assertions.assertThat;

public class WriteEnumsUsingIndexTest {

  @Test
  public void testEnabled() {
    ObjectMapper mapper = objectMapper(true);

    JsonNode node = mapper.valueToTree(getObject());
    assertThat(node.has("enum")).isTrue();
    assertThat(node.get("enum").isInt()).isTrue();
    assertThat(node.get("enum").intValue()).isEqualTo(2);
  }

  @Test
  public void testDisabled() {
    ObjectMapper mapper = objectMapper(false);

    JsonNode node = mapper.valueToTree(getObject());
    assertThat(node.has("enum")).isTrue();
    assertThat(node.get("enum").isTextual()).isTrue();
    assertThat(node.get("enum").textValue()).isEqualTo("TWO");
  }

  @Test
  public void testRoundTrip() {
    AllFields parsed = writeAndReadBack(objectMapper(true), getObject());

    assertThat(parsed.hasEnum()).isTrue();
    assertThat(parsed.getEnum()).isEqualTo(TestProtobuf.Enum.TWO);
  }

  private static AllFields getObject() {
    return AllFields.newBuilder().setEnum(TestProtobuf.Enum.TWO).build();
  }

  private static ObjectMapper objectMapper(boolean enabled) {
    if (enabled) {
      return camelCase().enable(SerializationFeature.WRITE_ENUMS_USING_INDEX);
    } else {
      return camelCase().disable(SerializationFeature.WRITE_ENUMS_USING_INDEX);
    }
  }
}
