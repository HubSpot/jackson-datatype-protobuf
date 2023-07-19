package com.hubspot.jackson.datatype.protobuf.builtin;

import static com.hubspot.jackson.datatype.protobuf.util.ObjectMapperHelper.camelCase;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.google.protobuf.NullValue;
import com.hubspot.jackson.datatype.protobuf.util.BuiltInProtobufs.HasNullValue;
import java.io.IOException;
import org.junit.Test;

public class NullValueTest {

  @Test
  public void itWritesNullValueWhenSetWithDefaultInclusion() throws IOException {
    HasNullValue message = HasNullValue
      .newBuilder()
      .setNullValue(NullValue.NULL_VALUE)
      .build();
    String json = camelCase().writeValueAsString(message);
    assertThat(json).isEqualTo("{\"nullValue\":null}");
  }

  @Test
  public void itWritesNullValueWhenNotSetWithDefaultInclusion() throws IOException {
    HasNullValue message = HasNullValue.newBuilder().build();
    String json = camelCase().writeValueAsString(message);
    assertThat(json).isEqualTo("{\"nullValue\":null}");
  }

  @Test
  public void itOmitsNullValueWhenSetWithNonDefaultInclusion() throws IOException {
    HasNullValue message = HasNullValue
      .newBuilder()
      .setNullValue(NullValue.NULL_VALUE)
      .build();
    String json = camelCase(Include.NON_DEFAULT).writeValueAsString(message);
    assertThat(json).isEqualTo("{}");
  }

  @Test
  public void itOmitsNullValueWhenNotSetWithNonDefaultInclusion() throws IOException {
    HasNullValue message = HasNullValue.newBuilder().build();
    String json = camelCase(Include.NON_DEFAULT).writeValueAsString(message);
    assertThat(json).isEqualTo("{}");
  }

  @Test
  public void itWritesNullValueWhenSetWithAlwaysInclusion() throws IOException {
    HasNullValue message = HasNullValue
      .newBuilder()
      .setNullValue(NullValue.NULL_VALUE)
      .build();
    String json = camelCase(Include.ALWAYS).writeValueAsString(message);
    assertThat(json).isEqualTo("{\"nullValue\":null}");
  }

  @Test
  public void itWritesNullValueWhenNotSetWithAlwaysInclusion() throws IOException {
    HasNullValue message = HasNullValue.newBuilder().build();
    String json = camelCase(Include.ALWAYS).writeValueAsString(message);
    assertThat(json).isEqualTo("{\"nullValue\":null}");
  }

  @Test
  public void itWritesNullValueWhenSetWithNonNullInclusion() throws IOException {
    HasNullValue message = HasNullValue
      .newBuilder()
      .setNullValue(NullValue.NULL_VALUE)
      .build();
    String json = camelCase(Include.NON_NULL).writeValueAsString(message);
    assertThat(json).isEqualTo("{\"nullValue\":null}");
  }

  @Test
  public void itWritesNullValueWhenNotSetWithNonNullInclusion() throws IOException {
    HasNullValue message = HasNullValue.newBuilder().build();
    String json = camelCase(Include.NON_NULL).writeValueAsString(message);
    assertThat(json).isEqualTo("{\"nullValue\":null}");
  }

  @Test
  public void itReadsNullValueWhenSet() throws IOException {
    String json = "{\"nullValue\":null}";
    HasNullValue message = camelCase().readValue(json, HasNullValue.class);
    assertThat(message.getNullValue()).isEqualTo(NullValue.NULL_VALUE);
  }

  @Test
  public void itReadsNullValueWhenNotSet() throws IOException {
    String json = "{}";
    HasNullValue message = camelCase().readValue(json, HasNullValue.class);
    assertThat(message.getNullValue()).isEqualTo(NullValue.NULL_VALUE);
  }
}
