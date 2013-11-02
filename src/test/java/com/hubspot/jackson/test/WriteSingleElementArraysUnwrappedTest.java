package com.hubspot.jackson.test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.hubspot.jackson.test.util.TestProtobuf.RepeatedFields;
import org.junit.Test;

import static com.hubspot.jackson.test.util.ObjectMapperHelper.camelCase;
import static org.fest.assertions.api.Assertions.assertThat;

public class WriteSingleElementArraysUnwrappedTest {

  @Test
  public void testEnabled() {
    ObjectMapper mapper = objectMapper(true);

    JsonNode node = mapper.valueToTree(getObject());
    assertThat(node.has("bool")).isTrue();
    assertThat(node.get("bool").isBoolean()).isTrue();
    assertThat(node.get("bool").booleanValue()).isFalse();
  }

  @Test
  public void testDisabled() {
    ObjectMapper mapper = objectMapper(false);

    JsonNode node = mapper.valueToTree(getObject());
    assertThat(node.has("bool")).isTrue();
    assertThat(node.get("bool").isArray()).isTrue();
    assertThat(node.get("bool").size()).isEqualTo(1);
    assertThat(node.get("bool").get(0).isBoolean()).isTrue();
    assertThat(node.get("bool").get(0).booleanValue()).isFalse();
  }

  private static RepeatedFields getObject() {
    return RepeatedFields.newBuilder().addBool(false).build();
  }

  private static ObjectMapper objectMapper(boolean enabled) {
    if (enabled) {
      return camelCase().enable(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED);
    } else {
      return camelCase().disable(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED);
    }
  }
}