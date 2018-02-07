package com.hubspot.jackson.datatype.protobuf.builtin;

import static com.hubspot.jackson.datatype.protobuf.util.ObjectMapperHelper.camelCase;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.google.protobuf.Duration;
import com.hubspot.jackson.datatype.protobuf.util.BuiltInProtobufs.HasDuration;

public class DurationTest {
  private static final Duration DURATION = Duration.newBuilder().setSeconds(30).build();

  @Test
  public void itWritesDurationWhenSetWithDefaultInclusion() throws IOException {
    HasDuration message = HasDuration.newBuilder().setDuration(DURATION).build();
    String json = camelCase().writeValueAsString(message);
    assertThat(json).isEqualTo("{\"duration\":\"30s\"}");
  }

  @Test
  public void itOmitsDurationWhenNotSetWithDefaultInclusion() throws IOException {
    HasDuration message = HasDuration.newBuilder().build();
    String json = camelCase().writeValueAsString(message);
    assertThat(json).isEqualTo("{}");
  }

  @Test
  public void itWritesDurationWhenSetWithNonDefaultInclusion() throws IOException {
    HasDuration message = HasDuration.newBuilder().setDuration(DURATION).build();
    String json = camelCase(Include.NON_DEFAULT).writeValueAsString(message);
    assertThat(json).isEqualTo("{\"duration\":\"30s\"}");
  }

  @Test
  public void itOmitsDurationWhenNotSetWithNonDefaultInclusion() throws IOException {
    HasDuration message = HasDuration.newBuilder().build();
    String json = camelCase(Include.NON_DEFAULT).writeValueAsString(message);
    assertThat(json).isEqualTo("{}");
  }

  @Test
  public void itWritesDurationSetWithAlwaysInclusion() throws IOException {
    HasDuration message = HasDuration.newBuilder().setDuration(DURATION).build();
    String json = camelCase(Include.ALWAYS).writeValueAsString(message);
    assertThat(json).isEqualTo("{\"duration\":\"30s\"}");
  }

  @Test
  public void itWritesNullWhenNotSetWithAlwaysInclusion() throws IOException {
    HasDuration message = HasDuration.newBuilder().build();
    String json = camelCase(Include.ALWAYS).writeValueAsString(message);
    assertThat(json).isEqualTo("{\"duration\":null}");
  }

  @Test
  public void itWritesDurationWhenSetWithNonNullInclusion() throws IOException {
    HasDuration message = HasDuration.newBuilder().setDuration(DURATION).build();
    String json = camelCase(Include.NON_NULL).writeValueAsString(message);
    assertThat(json).isEqualTo("{\"duration\":\"30s\"}");
  }

  @Test
  public void itOmitsDurationWhenNotSetWithNonNullInclusion() throws IOException {
    HasDuration message = HasDuration.newBuilder().build();
    String json = camelCase(Include.NON_NULL).writeValueAsString(message);
    assertThat(json).isEqualTo("{}");
  }

  @Test
  public void itSetsDurationWhenPresentInJson() throws IOException {
    String json = "{\"duration\":\"30s\"}";
    HasDuration message = camelCase().readValue(json, HasDuration.class);
    assertThat(message.hasDuration()).isTrue();
    assertThat(message.getDuration()).isEqualTo(DURATION);
  }

  @Test
  public void itDoesntSetDurationWhenNullInJson() throws IOException {
    String json = "{\"duration\":null}";
    HasDuration message = camelCase().readValue(json, HasDuration.class);
    assertThat(message.hasDuration()).isFalse();
  }

  @Test
  public void itDoesntSetDurationWhenMissingFromJson() throws IOException {
    String json = "{}";
    HasDuration message = camelCase().readValue(json, HasDuration.class);
    assertThat(message.hasDuration()).isFalse();
  }
}
