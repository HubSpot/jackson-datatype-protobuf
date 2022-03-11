package com.hubspot.jackson.datatype.protobuf;

import static com.hubspot.jackson.datatype.protobuf.util.ObjectMapperHelper.camelCase;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hubspot.jackson.datatype.protobuf.util.TestProtobuf;
import com.hubspot.jackson.datatype.protobuf.util.TestProtobuf.Enum;

public class ProtobufEnumAsNumberTest {

  @Test
  public void itSerializesToEnumNumber() {
    ObjectMapper mapper = camelCase();

    for (Enum anEnum : Enum.values()) {
      ObjectWithProtobufEnumField input = new ObjectWithProtobufEnumField();
      input.anEnum = anEnum;

      JsonNode node = mapper.valueToTree(input);

      assertThat(node.has("anEnum")).isTrue();
      assertThat(node.get("anEnum").isInt()).isTrue();
      assertThat(node.get("anEnum").intValue()).isEqualTo(anEnum.getNumber());
    }
  }

  @Test
  public void itDeserializesFromEnumNumber() throws JsonProcessingException {
    ObjectMapper mapper = camelCase();

    for (Enum anEnum : Enum.values()) {
      ObjectWithProtobufEnumField input = new ObjectWithProtobufEnumField();
      input.anEnum = anEnum;

      JsonNode node = mapper.valueToTree(input);

      ObjectWithProtobufEnumField output = mapper.treeToValue(node, ObjectWithProtobufEnumField.class);
      assertThat(output).isNotSameAs(input);
      assertThat(output.anEnum).isEqualTo(input.anEnum);
    }
  }

  @Test
  public void itFailsWhenSerializingNonProtobufEnumFields() {
    ObjectMapper mapper = camelCase();

    for (NonProtobufEnum anEnum : NonProtobufEnum.values()) {
      ObjectWithVanillaEnumField input = new ObjectWithVanillaEnumField();
      input.anEnum = anEnum;

      try {
        mapper.valueToTree(input);
        fail("expected an exception to be thrown");
      } catch (Exception e) {
        assertThat(e).isInstanceOf(IllegalArgumentException.class);
      }
    }
  }

  @Test
  public void itFailsWhenDeserializingNonProtobufEnumFields() {
    ObjectMapper mapper = camelCase();

    for (NonProtobufEnum anEnum : NonProtobufEnum.values()) {
      ObjectWithProtobufEnumField input = new ObjectWithProtobufEnumField();
      input.anEnum = Enum.valueOf(anEnum.name());

      JsonNode node = mapper.valueToTree(input);

      try {
        mapper.treeToValue(node, ObjectWithVanillaEnumField.class);
        fail("expected an exception to be thrown");
      } catch (Exception e) {
        assertThat(e).isInstanceOf(JsonMappingException.class);
      }
    }
  }

  private static class ObjectWithProtobufEnumField {

    @JsonSerialize(using = ProtobufEnumAsNumber.Serializer.class)
    @JsonDeserialize(using = ProtobufEnumAsNumber.Deserializer.class)
    private TestProtobuf.Enum anEnum;
  }

  private static class ObjectWithVanillaEnumField {

    @JsonSerialize(using = ProtobufEnumAsNumber.Serializer.class)
    @JsonDeserialize(using = ProtobufEnumAsNumber.Deserializer.class)
    private NonProtobufEnum anEnum;
  }

  private enum NonProtobufEnum {
    ONE, TWO
  }
}
