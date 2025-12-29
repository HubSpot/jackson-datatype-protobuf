package com.hubspot.jackson.datatype.protobuf.builtin;

import static com.hubspot.jackson.datatype.protobuf.util.ObjectMapperHelper.camelCase;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.google.protobuf.Duration;
import com.hubspot.jackson.datatype.protobuf.util.BuiltInProtobufs.HasDuration;
import org.junit.Test;

public class DurationTest {

  private static final Duration DURATION = Duration.newBuilder().setSeconds(30).build();

  @Test
  public void itWritesDurationWhenSetWithDefaultInclusion() {
    HasDuration message = HasDuration.newBuilder().setDuration(DURATION).build();
    String json = camelCase().writeValueAsString(message);
    assertThat(json).isEqualTo("{\"duration\":\"30s\"}");
  }

  @Test
  public void itWritesZeroWhenSetToDefaultInstanceWithDefaultInclusion() {
    HasDuration message = HasDuration
      .newBuilder()
      .setDuration(Duration.getDefaultInstance())
      .build();
    String json = camelCase().writeValueAsString(message);
    assertThat(json).isEqualTo("{\"duration\":\"0s\"}");
  }

  @Test
  public void itOmitsDurationWhenNotSetWithDefaultInclusion() {
    HasDuration message = HasDuration.newBuilder().build();
    String json = camelCase().writeValueAsString(message);
    assertThat(json).isEqualTo("{}");
  }

  @Test
  public void itWritesDurationWhenSetWithNonDefaultInclusion() {
    HasDuration message = HasDuration.newBuilder().setDuration(DURATION).build();
    String json = camelCase(Include.NON_DEFAULT).writeValueAsString(message);
    assertThat(json).isEqualTo("{\"duration\":\"30s\"}");
  }

  @Test
  public void itWritesZeroWhenSetToDefaultInstanceWithNonDefaultInclusion() {
    HasDuration message = HasDuration
      .newBuilder()
      .setDuration(Duration.getDefaultInstance())
      .build();
    String json = camelCase(Include.NON_DEFAULT).writeValueAsString(message);
    assertThat(json).isEqualTo("{\"duration\":\"0s\"}");
  }

  @Test
  public void itOmitsDurationWhenNotSetWithNonDefaultInclusion() {
    HasDuration message = HasDuration.newBuilder().build();
    String json = camelCase(Include.NON_DEFAULT).writeValueAsString(message);
    assertThat(json).isEqualTo("{}");
  }

  @Test
  public void itWritesDurationSetWithAlwaysInclusion() {
    HasDuration message = HasDuration.newBuilder().setDuration(DURATION).build();
    String json = camelCase(Include.ALWAYS).writeValueAsString(message);
    assertThat(json).isEqualTo("{\"duration\":\"30s\"}");
  }

  @Test
  public void itWritesZeroWhenSetToDefaultInstanceWithAlwaysInclusion() {
    HasDuration message = HasDuration
      .newBuilder()
      .setDuration(Duration.getDefaultInstance())
      .build();
    String json = camelCase(Include.ALWAYS).writeValueAsString(message);
    assertThat(json).isEqualTo("{\"duration\":\"0s\"}");
  }

  @Test
  public void itWritesNullWhenNotSetWithAlwaysInclusion() {
    HasDuration message = HasDuration.newBuilder().build();
    String json = camelCase(Include.ALWAYS).writeValueAsString(message);
    assertThat(json).isEqualTo("{\"duration\":null}");
  }

  @Test
  public void itWritesDurationWhenSetWithNonNullInclusion() {
    HasDuration message = HasDuration.newBuilder().setDuration(DURATION).build();
    String json = camelCase(Include.NON_NULL).writeValueAsString(message);
    assertThat(json).isEqualTo("{\"duration\":\"30s\"}");
  }

  @Test
  public void itWritesZeroWhenSetToDefaultInstanceWithNonNullInclusion() {
    HasDuration message = HasDuration
      .newBuilder()
      .setDuration(Duration.getDefaultInstance())
      .build();
    String json = camelCase(Include.NON_NULL).writeValueAsString(message);
    assertThat(json).isEqualTo("{\"duration\":\"0s\"}");
  }

  @Test
  public void itOmitsDurationWhenNotSetWithNonNullInclusion() {
    HasDuration message = HasDuration.newBuilder().build();
    String json = camelCase(Include.NON_NULL).writeValueAsString(message);
    assertThat(json).isEqualTo("{}");
  }

  @Test
  public void itSetsDurationWhenPresentInJson() {
    String json = "{\"duration\":\"30s\"}";
    HasDuration message = camelCase().readValue(json, HasDuration.class);
    assertThat(message.hasDuration()).isTrue();
    assertThat(message.getDuration()).isEqualTo(DURATION);
  }

  @Test
  public void itSetsDurationWhenZeroInJson() {
    String json = "{\"duration\":\"0s\"}";
    HasDuration message = camelCase().readValue(json, HasDuration.class);
    assertThat(message.hasDuration()).isTrue();
    assertThat(message.getDuration()).isEqualTo(Duration.getDefaultInstance());
  }

  @Test
  public void itDoesntSetDurationWhenNullInJson() {
    String json = "{\"duration\":null}";
    HasDuration message = camelCase().readValue(json, HasDuration.class);
    assertThat(message.hasDuration()).isFalse();
  }

  @Test
  public void itDoesntSetDurationWhenMissingFromJson() {
    String json = "{}";
    HasDuration message = camelCase().readValue(json, HasDuration.class);
    assertThat(message.hasDuration()).isFalse();
  }
}
