package com.hubspot.jackson.datatype.protobuf.builtin;

import static com.hubspot.jackson.datatype.protobuf.util.ObjectMapperHelper.camelCase;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.google.protobuf.Timestamp;
import com.hubspot.jackson.datatype.protobuf.util.BuiltInProtobufs.HasTimestamp;
import java.io.IOException;
import org.junit.Test;

public class TimestampTest {

  private static final Timestamp TIMESTAMP = Timestamp
    .newBuilder()
    .setSeconds(946684800)
    .build();

  @Test
  public void itWritesTimestampWhenSetWithDefaultInclusion() throws IOException {
    HasTimestamp message = HasTimestamp.newBuilder().setTimestamp(TIMESTAMP).build();
    String json = camelCase().writeValueAsString(message);
    assertThat(json).isEqualTo("{\"timestamp\":\"2000-01-01T00:00:00Z\"}");
  }

  @Test
  public void itWritesZeroWhenSetToDefaultInstanceWithDefaultInclusion()
    throws IOException {
    HasTimestamp message = HasTimestamp
      .newBuilder()
      .setTimestamp(Timestamp.getDefaultInstance())
      .build();
    String json = camelCase().writeValueAsString(message);
    assertThat(json).isEqualTo("{\"timestamp\":\"1970-01-01T00:00:00Z\"}");
  }

  @Test
  public void itOmitsTimestampWhenNotSetWithDefaultInclusion() throws IOException {
    HasTimestamp message = HasTimestamp.newBuilder().build();
    String json = camelCase().writeValueAsString(message);
    assertThat(json).isEqualTo("{}");
  }

  @Test
  public void itWritesTimestampWhenSetWithNonDefaultInclusion() throws IOException {
    HasTimestamp message = HasTimestamp.newBuilder().setTimestamp(TIMESTAMP).build();
    String json = camelCase(Include.NON_DEFAULT).writeValueAsString(message);
    assertThat(json).isEqualTo("{\"timestamp\":\"2000-01-01T00:00:00Z\"}");
  }

  @Test
  public void itWritesZeroWhenSetToDefaultInstanceWithNonDefaultInclusion()
    throws IOException {
    HasTimestamp message = HasTimestamp
      .newBuilder()
      .setTimestamp(Timestamp.getDefaultInstance())
      .build();
    String json = camelCase(Include.NON_DEFAULT).writeValueAsString(message);
    assertThat(json).isEqualTo("{\"timestamp\":\"1970-01-01T00:00:00Z\"}");
  }

  @Test
  public void itOmitsTimestampWhenNotSetWithNonDefaultInclusion() throws IOException {
    HasTimestamp message = HasTimestamp.newBuilder().build();
    String json = camelCase(Include.NON_DEFAULT).writeValueAsString(message);
    assertThat(json).isEqualTo("{}");
  }

  @Test
  public void itWritesTimestampSetWithAlwaysInclusion() throws IOException {
    HasTimestamp message = HasTimestamp.newBuilder().setTimestamp(TIMESTAMP).build();
    String json = camelCase(Include.ALWAYS).writeValueAsString(message);
    assertThat(json).isEqualTo("{\"timestamp\":\"2000-01-01T00:00:00Z\"}");
  }

  @Test
  public void itWritesZeroWhenSetToDefaultInstanceWithAlwaysInclusion()
    throws IOException {
    HasTimestamp message = HasTimestamp
      .newBuilder()
      .setTimestamp(Timestamp.getDefaultInstance())
      .build();
    String json = camelCase(Include.ALWAYS).writeValueAsString(message);
    assertThat(json).isEqualTo("{\"timestamp\":\"1970-01-01T00:00:00Z\"}");
  }

  @Test
  public void itWritesNullWhenNotSetWithAlwaysInclusion() throws IOException {
    HasTimestamp message = HasTimestamp.newBuilder().build();
    String json = camelCase(Include.ALWAYS).writeValueAsString(message);
    assertThat(json).isEqualTo("{\"timestamp\":null}");
  }

  @Test
  public void itWritesTimestampWhenSetWithNonNullInclusion() throws IOException {
    HasTimestamp message = HasTimestamp.newBuilder().setTimestamp(TIMESTAMP).build();
    String json = camelCase(Include.NON_NULL).writeValueAsString(message);
    assertThat(json).isEqualTo("{\"timestamp\":\"2000-01-01T00:00:00Z\"}");
  }

  @Test
  public void itWritesZeroWhenSetToDefaultInstanceWithNonNullInclusion()
    throws IOException {
    HasTimestamp message = HasTimestamp
      .newBuilder()
      .setTimestamp(Timestamp.getDefaultInstance())
      .build();
    String json = camelCase(Include.NON_NULL).writeValueAsString(message);
    assertThat(json).isEqualTo("{\"timestamp\":\"1970-01-01T00:00:00Z\"}");
  }

  @Test
  public void itOmitsTimestampWhenNotSetWithNonNullInclusion() throws IOException {
    HasTimestamp message = HasTimestamp.newBuilder().build();
    String json = camelCase(Include.NON_NULL).writeValueAsString(message);
    assertThat(json).isEqualTo("{}");
  }

  @Test
  public void itSetsTimestampWhenPresentInJson() throws IOException {
    String json = "{\"timestamp\":\"2000-01-01T00:00:00Z\"}";
    HasTimestamp message = camelCase().readValue(json, HasTimestamp.class);
    assertThat(message.hasTimestamp()).isTrue();
    assertThat(message.getTimestamp()).isEqualTo(TIMESTAMP);
  }

  @Test
  public void itSetsTimestampWhenZeroInJson() throws IOException {
    String json = "{\"timestamp\":\"1970-01-01T00:00:00Z\"}";
    HasTimestamp message = camelCase().readValue(json, HasTimestamp.class);
    assertThat(message.hasTimestamp()).isTrue();
    assertThat(message.getTimestamp()).isEqualTo(Timestamp.getDefaultInstance());
  }

  @Test
  public void itDoesntSetTimestampWhenNullInJson() throws IOException {
    String json = "{\"timestamp\":null}";
    HasTimestamp message = camelCase().readValue(json, HasTimestamp.class);
    assertThat(message.hasTimestamp()).isFalse();
  }

  @Test
  public void itDoesntSetTimestampWhenMissingFromJson() throws IOException {
    String json = "{}";
    HasTimestamp message = camelCase().readValue(json, HasTimestamp.class);
    assertThat(message.hasTimestamp()).isFalse();
  }
}
