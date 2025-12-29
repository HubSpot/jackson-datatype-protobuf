package com.hubspot.jackson.datatype.protobuf.builtin;

import static com.hubspot.jackson.datatype.protobuf.util.ObjectMapperHelper.camelCase;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.google.protobuf.NullValue;
import com.hubspot.jackson.datatype.protobuf.util.BuiltInProtobufs.HasNullValue;
import org.junit.Test;

public class NullValueTest {

  @Test
  public void itWritesNullValueWhenSetWithDefaultInclusion() {
    HasNullValue message = HasNullValue
      .newBuilder()
      .setNullValue(NullValue.NULL_VALUE)
      .build();
    String json = camelCase().writeValueAsString(message);
    assertThat(json).isEqualTo("{\"nullValue\":null}");
  }

  @Test
  public void itWritesNullValueWhenNotSetWithDefaultInclusion() {
    HasNullValue message = HasNullValue.newBuilder().build();
    String json = camelCase().writeValueAsString(message);
    assertThat(json).isEqualTo("{\"nullValue\":null}");
  }

  @Test
  public void itOmitsNullValueWhenSetWithNonDefaultInclusion() {
    HasNullValue message = HasNullValue
      .newBuilder()
      .setNullValue(NullValue.NULL_VALUE)
      .build();
    String json = camelCase(Include.NON_DEFAULT).writeValueAsString(message);
    assertThat(json).isEqualTo("{}");
  }

  @Test
  public void itOmitsNullValueWhenNotSetWithNonDefaultInclusion() {
    HasNullValue message = HasNullValue.newBuilder().build();
    String json = camelCase(Include.NON_DEFAULT).writeValueAsString(message);
    assertThat(json).isEqualTo("{}");
  }

  @Test
  public void itWritesNullValueWhenSetWithAlwaysInclusion() {
    HasNullValue message = HasNullValue
      .newBuilder()
      .setNullValue(NullValue.NULL_VALUE)
      .build();
    String json = camelCase(Include.ALWAYS).writeValueAsString(message);
    assertThat(json).isEqualTo("{\"nullValue\":null}");
  }

  @Test
  public void itWritesNullValueWhenNotSetWithAlwaysInclusion() {
    HasNullValue message = HasNullValue.newBuilder().build();
    String json = camelCase(Include.ALWAYS).writeValueAsString(message);
    assertThat(json).isEqualTo("{\"nullValue\":null}");
  }

  @Test
  public void itWritesNullValueWhenSetWithNonNullInclusion() {
    HasNullValue message = HasNullValue
      .newBuilder()
      .setNullValue(NullValue.NULL_VALUE)
      .build();
    String json = camelCase(Include.NON_NULL).writeValueAsString(message);
    assertThat(json).isEqualTo("{\"nullValue\":null}");
  }

  @Test
  public void itWritesNullValueWhenNotSetWithNonNullInclusion() {
    HasNullValue message = HasNullValue.newBuilder().build();
    String json = camelCase(Include.NON_NULL).writeValueAsString(message);
    assertThat(json).isEqualTo("{\"nullValue\":null}");
  }

  @Test
  public void itReadsNullValueWhenSet() {
    String json = "{\"nullValue\":null}";
    HasNullValue message = camelCase().readValue(json, HasNullValue.class);
    assertThat(message.getNullValue()).isEqualTo(NullValue.NULL_VALUE);
  }

  @Test
  public void itReadsNullValueWhenNotSet() {
    String json = "{}";
    HasNullValue message = camelCase().readValue(json, HasNullValue.class);
    assertThat(message.getNullValue()).isEqualTo(NullValue.NULL_VALUE);
  }
}
