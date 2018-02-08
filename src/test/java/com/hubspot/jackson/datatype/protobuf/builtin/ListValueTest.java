package com.hubspot.jackson.datatype.protobuf.builtin;

import static com.hubspot.jackson.datatype.protobuf.util.ObjectMapperHelper.camelCase;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.google.protobuf.ListValue;
import com.google.protobuf.Value;
import com.hubspot.jackson.datatype.protobuf.util.BuiltInProtobufs.HasListValue;

public class ListValueTest {
  private static final Value VALUE = Value.newBuilder().setStringValue("test").build();
  private static final ListValue LIST_VALUE = ListValue.newBuilder().addValues(VALUE).build();

  @Test
  public void itWritesListValueWhenSetWithDefaultInclusion() throws IOException {
    HasListValue message = HasListValue.newBuilder().setListValue(LIST_VALUE).build();
    String json = camelCase().writeValueAsString(message);
    assertThat(json).isEqualTo("{\"listValue\":[\"test\"]}");
  }

  @Test
  public void itWritesEmptyArrayWhenSetToDefaultInstanceWithDefaultInclusion() throws IOException {
    HasListValue message = HasListValue.newBuilder().setListValue(ListValue.getDefaultInstance()).build();
    String json = camelCase().writeValueAsString(message);
    assertThat(json).isEqualTo("{\"listValue\":[]}");
  }

  @Test
  public void itOmitsListValueWhenNotSetWithDefaultInclusion() throws IOException {
    HasListValue message = HasListValue.newBuilder().build();
    String json = camelCase().writeValueAsString(message);
    assertThat(json).isEqualTo("{}");
  }

  @Test
  public void itWritesListValueWhenSetWithNonDefaultInclusion() throws IOException {
    HasListValue message = HasListValue.newBuilder().setListValue(LIST_VALUE).build();
    String json = camelCase(Include.NON_DEFAULT).writeValueAsString(message);
    assertThat(json).isEqualTo("{\"listValue\":[\"test\"]}");
  }

  @Test
  public void itWritesEmptyArrayWhenSetToDefaultInstanceWithNonDefaultInclusion() throws IOException {
    HasListValue message = HasListValue.newBuilder().setListValue(ListValue.getDefaultInstance()).build();
    String json = camelCase(Include.NON_DEFAULT).writeValueAsString(message);
    assertThat(json).isEqualTo("{\"listValue\":[]}");
  }

  @Test
  public void itOmitsListValueWhenNotSetWithNonDefaultInclusion() throws IOException {
    HasListValue message = HasListValue.newBuilder().build();
    String json = camelCase(Include.NON_DEFAULT).writeValueAsString(message);
    assertThat(json).isEqualTo("{}");
  }

  @Test
  public void itWritesListValueSetWithAlwaysInclusion() throws IOException {
    HasListValue message = HasListValue.newBuilder().setListValue(LIST_VALUE).build();
    String json = camelCase(Include.ALWAYS).writeValueAsString(message);
    assertThat(json).isEqualTo("{\"listValue\":[\"test\"]}");
  }

  @Test
  public void itWritesEmptyArrayWhenSetToDefaultInstanceWithAlwaysInclusion() throws IOException {
    HasListValue message = HasListValue.newBuilder().setListValue(ListValue.getDefaultInstance()).build();
    String json = camelCase(Include.ALWAYS).writeValueAsString(message);
    assertThat(json).isEqualTo("{\"listValue\":[]}");
  }

  @Test
  public void itWritesNullWhenNotSetWithAlwaysInclusion() throws IOException {
    HasListValue message = HasListValue.newBuilder().build();
    String json = camelCase(Include.ALWAYS).writeValueAsString(message);
    assertThat(json).isEqualTo("{\"listValue\":null}");
  }

  @Test
  public void itWritesListValueWhenSetWithNonNullInclusion() throws IOException {
    HasListValue message = HasListValue.newBuilder().setListValue(LIST_VALUE).build();
    String json = camelCase(Include.NON_NULL).writeValueAsString(message);
    assertThat(json).isEqualTo("{\"listValue\":[\"test\"]}");
  }

  @Test
  public void itWritesEmptyArrayWhenSetToDefaultInstanceWithNonNullInclusion() throws IOException {
    HasListValue message = HasListValue.newBuilder().setListValue(ListValue.getDefaultInstance()).build();
    String json = camelCase(Include.NON_NULL).writeValueAsString(message);
    assertThat(json).isEqualTo("{\"listValue\":[]}");
  }

  @Test
  public void itOmitsListValueWhenNotSetWithNonNullInclusion() throws IOException {
    HasListValue message = HasListValue.newBuilder().build();
    String json = camelCase(Include.NON_NULL).writeValueAsString(message);
    assertThat(json).isEqualTo("{}");
  }

  @Test
  public void itSetsListValueWhenPresentInJson() throws IOException {
    String json = "{\"listValue\":[\"test\"]}";
    HasListValue message = camelCase().readValue(json, HasListValue.class);
    assertThat(message.hasListValue()).isTrue();
    assertThat(message.getListValue()).isEqualTo(LIST_VALUE);
  }

  @Test
  public void itSetsListValueWhenEmptyInJson() throws IOException {
    String json = "{\"listValue\":[]}";
    HasListValue message = camelCase().readValue(json, HasListValue.class);
    assertThat(message.hasListValue()).isTrue();
    assertThat(message.getListValue()).isEqualTo(ListValue.getDefaultInstance());
  }

  @Test
  public void itDoesntSetListValueWhenNullInJson() throws IOException {
    String json = "{\"listValue\":null}";
    HasListValue message = camelCase().readValue(json, HasListValue.class);
    assertThat(message.hasListValue()).isFalse();
  }

  @Test
  public void itDoesntSetListValueWhenMissingFromJson() throws IOException {
    String json = "{}";
    HasListValue message = camelCase().readValue(json, HasListValue.class);
    assertThat(message.hasListValue()).isFalse();
  }
}
