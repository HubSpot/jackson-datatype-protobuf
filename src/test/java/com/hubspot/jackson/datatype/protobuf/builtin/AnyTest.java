package com.hubspot.jackson.datatype.protobuf.builtin;

import static com.hubspot.jackson.datatype.protobuf.util.ObjectMapperHelper.camelCase;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.protobuf.Any;
import com.google.protobuf.Value;
import com.hubspot.jackson.datatype.protobuf.util.BuiltInProtobufs.HasAny;

public class AnyTest {
  private static final String TYPE_URL = "type.googleapis.com/google.protobuf.Value";
  private static final Value VALUE = Value.newBuilder().setStringValue("test").build();
  private static final Any ANY = Any
          .newBuilder()
          .setTypeUrl(TYPE_URL)
          .setValue(VALUE.toByteString())
          .build();

  @Test
  public void itWritesDurationWhenSetWithDefaultInclusion() throws IOException {
    HasAny message = HasAny.newBuilder().setAny(ANY).build();
    String json = camelCase().writeValueAsString(message);
    assertThat(camelCase().readTree(json)).isEqualTo(anyNode());
  }

  @Test
  public void itWritesZeroWhenSetToDefaultInstanceWithDefaultInclusion() throws IOException {
    HasAny message = HasAny.newBuilder().setAny(Any.getDefaultInstance()).build();
    String json = camelCase().writeValueAsString(message);
    assertThat(camelCase().readTree(json)).isEqualTo(defaultNode());
  }

  @Test
  public void itOmitsDurationWhenNotSetWithDefaultInclusion() throws IOException {
    HasAny message = HasAny.newBuilder().build();
    String json = camelCase().writeValueAsString(message);
    assertThat(json).isEqualTo("{}");
  }

  @Test
  public void itWritesDurationWhenSetWithNonDefaultInclusion() throws IOException {
    HasAny message = HasAny.newBuilder().setAny(ANY).build();
    String json = camelCase().writeValueAsString(message);
    assertThat(camelCase().readTree(json)).isEqualTo(anyNode());
  }

  @Test
  public void itWritesZeroWhenSetToDefaultInstanceWithNonDefaultInclusion() throws IOException {
    HasAny message = HasAny.newBuilder().setAny(Any.getDefaultInstance()).build();
    String json = camelCase().writeValueAsString(message);
    assertThat(camelCase().readTree(json)).isEqualTo(defaultNode());
  }

  @Test
  public void itOmitsDurationWhenNotSetWithNonDefaultInclusion() throws IOException {
    HasAny message = HasAny.newBuilder().build();
    String json = camelCase(Include.NON_DEFAULT).writeValueAsString(message);
    assertThat(json).isEqualTo("{}");
  }

  @Test
  public void itWritesDurationSetWithAlwaysInclusion() throws IOException {
    HasAny message = HasAny.newBuilder().setAny(ANY).build();
    String json = camelCase().writeValueAsString(message);
    assertThat(camelCase().readTree(json)).isEqualTo(anyNode());
  }

  @Test
  public void itWritesZeroWhenSetToDefaultInstanceWithAlwaysInclusion() throws IOException {
    HasAny message = HasAny.newBuilder().setAny(Any.getDefaultInstance()).build();
    String json = camelCase().writeValueAsString(message);
    assertThat(camelCase().readTree(json)).isEqualTo(defaultNode());
  }

  @Test
  public void itWritesNullWhenNotSetWithAlwaysInclusion() throws IOException {
    HasAny message = HasAny.newBuilder().build();
    String json = camelCase(Include.ALWAYS).writeValueAsString(message);
    assertThat(json).isEqualTo("{\"any\":null}");
  }

  @Test
  public void itWritesDurationWhenSetWithNonNullInclusion() throws IOException {
    HasAny message = HasAny.newBuilder().setAny(ANY).build();
    String json = camelCase().writeValueAsString(message);
    assertThat(camelCase().readTree(json)).isEqualTo(anyNode());
  }

  @Test
  public void itWritesZeroWhenSetToDefaultInstanceWithNonNullInclusion() throws IOException {
    HasAny message = HasAny.newBuilder().setAny(Any.getDefaultInstance()).build();
    String json = camelCase().writeValueAsString(message);
    assertThat(camelCase().readTree(json)).isEqualTo(defaultNode());
  }

  @Test
  public void itOmitsDurationWhenNotSetWithNonNullInclusion() throws IOException {
    HasAny message = HasAny.newBuilder().build();
    String json = camelCase(Include.NON_NULL).writeValueAsString(message);
    assertThat(json).isEqualTo("{}");
  }

  @Test
  public void itSetsDurationWhenPresentInJson() throws IOException {
    String json = camelCase().writeValueAsString(anyNode());
    HasAny message = camelCase().readValue(json, HasAny.class);
    assertThat(message.hasAny()).isTrue();
    assertThat(message.getAny()).isEqualTo(ANY);
  }

  @Test
  public void itSetsDurationWhenZeroInJson() throws IOException {
    String json = camelCase().writeValueAsString(defaultNode());
    HasAny message = camelCase().readValue(json, HasAny.class);
    assertThat(message.hasAny()).isTrue();
    assertThat(message.getAny()).isEqualTo(Any.getDefaultInstance());
  }

  @Test
  public void itDoesntSetDurationWhenNullInJson() throws IOException {
    String json = "{\"any\":null}";
    HasAny message = camelCase().readValue(json, HasAny.class);
    assertThat(message.hasAny()).isFalse();
  }

  @Test
  public void itDoesntSetDurationWhenMissingFromJson() throws IOException {
    String json = "{}";
    HasAny message = camelCase().readValue(json, HasAny.class);
    assertThat(message.hasAny()).isFalse();
  }

  private static JsonNode anyNode() {
    String base64 = camelCase().getSerializationConfig().getBase64Variant().encode(VALUE.toByteArray());
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
