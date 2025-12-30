package com.hubspot.jackson3.datatype.protobuf.builtin;

import static com.hubspot.jackson3.datatype.protobuf.util.ObjectMapperHelper.camelCase;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.google.protobuf.Any;
import com.google.protobuf.Value;
import com.hubspot.jackson3.datatype.protobuf.util.BuiltInProtobufs.HasAny;
import org.junit.Test;
import tools.jackson.databind.JsonNode;

public class AnyTest {

  private static final String TYPE_URL = "type.googleapis.com/google.protobuf.Value";
  private static final Value VALUE = Value.newBuilder().setStringValue("test").build();
  private static final Any ANY = Any
    .newBuilder()
    .setTypeUrl(TYPE_URL)
    .setValue(VALUE.toByteString())
    .build();

  @Test
  public void itWritesDurationWhenSetWithDefaultInclusion() {
    HasAny message = HasAny.newBuilder().setAny(ANY).build();
    String json = camelCase().writeValueAsString(message);
    assertThat(camelCase().readTree(json)).isEqualTo(anyNode());
  }

  @Test
  public void itWritesZeroWhenSetToDefaultInstanceWithDefaultInclusion() {
    HasAny message = HasAny.newBuilder().setAny(Any.getDefaultInstance()).build();
    String json = camelCase().writeValueAsString(message);
    assertThat(camelCase().readTree(json)).isEqualTo(defaultNode());
  }

  @Test
  public void itOmitsDurationWhenNotSetWithDefaultInclusion() {
    HasAny message = HasAny.newBuilder().build();
    String json = camelCase().writeValueAsString(message);
    assertThat(json).isEqualTo("{}");
  }

  @Test
  public void itWritesDurationWhenSetWithNonDefaultInclusion() {
    HasAny message = HasAny.newBuilder().setAny(ANY).build();
    String json = camelCase().writeValueAsString(message);
    assertThat(camelCase().readTree(json)).isEqualTo(anyNode());
  }

  @Test
  public void itWritesZeroWhenSetToDefaultInstanceWithNonDefaultInclusion() {
    HasAny message = HasAny.newBuilder().setAny(Any.getDefaultInstance()).build();
    String json = camelCase().writeValueAsString(message);
    assertThat(camelCase().readTree(json)).isEqualTo(defaultNode());
  }

  @Test
  public void itOmitsDurationWhenNotSetWithNonDefaultInclusion() {
    HasAny message = HasAny.newBuilder().build();
    String json = camelCase(Include.NON_DEFAULT).writeValueAsString(message);
    assertThat(json).isEqualTo("{}");
  }

  @Test
  public void itWritesDurationSetWithAlwaysInclusion() {
    HasAny message = HasAny.newBuilder().setAny(ANY).build();
    String json = camelCase().writeValueAsString(message);
    assertThat(camelCase().readTree(json)).isEqualTo(anyNode());
  }

  @Test
  public void itWritesZeroWhenSetToDefaultInstanceWithAlwaysInclusion() {
    HasAny message = HasAny.newBuilder().setAny(Any.getDefaultInstance()).build();
    String json = camelCase().writeValueAsString(message);
    assertThat(camelCase().readTree(json)).isEqualTo(defaultNode());
  }

  @Test
  public void itWritesNullWhenNotSetWithAlwaysInclusion() {
    HasAny message = HasAny.newBuilder().build();
    String json = camelCase(Include.ALWAYS).writeValueAsString(message);
    assertThat(json).isEqualTo("{\"any\":null}");
  }

  @Test
  public void itWritesDurationWhenSetWithNonNullInclusion() {
    HasAny message = HasAny.newBuilder().setAny(ANY).build();
    String json = camelCase().writeValueAsString(message);
    assertThat(camelCase().readTree(json)).isEqualTo(anyNode());
  }

  @Test
  public void itWritesZeroWhenSetToDefaultInstanceWithNonNullInclusion() {
    HasAny message = HasAny.newBuilder().setAny(Any.getDefaultInstance()).build();
    String json = camelCase().writeValueAsString(message);
    assertThat(camelCase().readTree(json)).isEqualTo(defaultNode());
  }

  @Test
  public void itOmitsDurationWhenNotSetWithNonNullInclusion() {
    HasAny message = HasAny.newBuilder().build();
    String json = camelCase(Include.NON_NULL).writeValueAsString(message);
    assertThat(json).isEqualTo("{}");
  }

  @Test
  public void itSetsDurationWhenPresentInJson() {
    String json = camelCase().writeValueAsString(anyNode());
    HasAny message = camelCase().readValue(json, HasAny.class);
    assertThat(message.hasAny()).isTrue();
    assertThat(message.getAny()).isEqualTo(ANY);
  }

  @Test
  public void itSetsDurationWhenZeroInJson() {
    String json = camelCase().writeValueAsString(defaultNode());
    HasAny message = camelCase().readValue(json, HasAny.class);
    assertThat(message.hasAny()).isTrue();
    assertThat(message.getAny()).isEqualTo(Any.getDefaultInstance());
  }

  @Test
  public void itDoesntSetDurationWhenNullInJson() {
    String json = "{\"any\":null}";
    HasAny message = camelCase().readValue(json, HasAny.class);
    assertThat(message.hasAny()).isFalse();
  }

  @Test
  public void itDoesntSetDurationWhenMissingFromJson() {
    String json = "{}";
    HasAny message = camelCase().readValue(json, HasAny.class);
    assertThat(message.hasAny()).isFalse();
  }

  private static JsonNode anyNode() {
    String base64 = camelCase()
      .serializationConfig()
      .getBase64Variant()
      .encode(VALUE.toByteArray());
    JsonNode valueNode = camelCase()
      .createObjectNode()
      .put("typeUrl", TYPE_URL)
      .put("value", base64);
    return camelCase().createObjectNode().set("any", valueNode);
  }

  private static JsonNode defaultNode() {
    JsonNode valueNode = camelCase()
      .createObjectNode()
      .put("typeUrl", "")
      .put("value", "");
    return camelCase().createObjectNode().set("any", valueNode);
  }
}
