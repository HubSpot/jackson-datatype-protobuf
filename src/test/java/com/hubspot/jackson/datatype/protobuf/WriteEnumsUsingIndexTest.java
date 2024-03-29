package com.hubspot.jackson.datatype.protobuf;

import static com.hubspot.jackson.datatype.protobuf.util.ObjectMapperHelper.create;
import static com.hubspot.jackson.datatype.protobuf.util.ObjectMapperHelper.writeAndReadBack;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.hubspot.jackson.datatype.protobuf.util.TestProtobuf;
import com.hubspot.jackson.datatype.protobuf.util.TestProtobuf.AllFields;
import org.junit.Test;

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
      return create().enable(SerializationFeature.WRITE_ENUMS_USING_INDEX);
    } else {
      return create().disable(SerializationFeature.WRITE_ENUMS_USING_INDEX);
    }
  }
}
