package com.hubspot.jackson3.datatype.protobuf.builtin;

import static com.hubspot.jackson3.datatype.protobuf.util.ObjectMapperHelper.camelCase;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.google.protobuf.FieldMask;
import com.hubspot.jackson3.datatype.protobuf.util.BuiltInProtobufs.HasFieldMask;
import org.junit.Test;

public class FieldMaskTest {

  private static final FieldMask FIELD_MASK = FieldMask
    .newBuilder()
    .addPaths("path_one")
    .addPaths("path_two")
    .build();

  @Test
  public void itWritesFieldMaskWhenSetWithDefaultInclusion() {
    HasFieldMask message = HasFieldMask.newBuilder().setFieldMask(FIELD_MASK).build();
    String json = camelCase().writeValueAsString(message);
    assertThat(json).isEqualTo("{\"fieldMask\":\"pathOne,pathTwo\"}");
  }

  @Test
  public void itWritesEmptyStringWhenSetToDefaultInstanceWithDefaultInclusion() {
    HasFieldMask message = HasFieldMask
      .newBuilder()
      .setFieldMask(FieldMask.getDefaultInstance())
      .build();
    String json = camelCase().writeValueAsString(message);
    assertThat(json).isEqualTo("{\"fieldMask\":\"\"}");
  }

  @Test
  public void itOmitsFieldMaskWhenNotSetWithDefaultInclusion() {
    HasFieldMask message = HasFieldMask.newBuilder().build();
    String json = camelCase().writeValueAsString(message);
    assertThat(json).isEqualTo("{}");
  }

  @Test
  public void itWritesFieldMaskWhenSetWithNonDefaultInclusion() {
    HasFieldMask message = HasFieldMask.newBuilder().setFieldMask(FIELD_MASK).build();
    String json = camelCase(Include.NON_DEFAULT).writeValueAsString(message);
    assertThat(json).isEqualTo("{\"fieldMask\":\"pathOne,pathTwo\"}");
  }

  @Test
  public void itWritesEmptyStringWhenSetToDefaultInstanceWithNonDefaultInclusion() {
    HasFieldMask message = HasFieldMask
      .newBuilder()
      .setFieldMask(FieldMask.getDefaultInstance())
      .build();
    String json = camelCase(Include.NON_DEFAULT).writeValueAsString(message);
    assertThat(json).isEqualTo("{\"fieldMask\":\"\"}");
  }

  @Test
  public void itOmitsFieldMaskWhenNotSetWithNonDefaultInclusion() {
    HasFieldMask message = HasFieldMask.newBuilder().build();
    String json = camelCase(Include.NON_DEFAULT).writeValueAsString(message);
    assertThat(json).isEqualTo("{}");
  }

  @Test
  public void itWritesFieldMaskSetWithAlwaysInclusion() {
    HasFieldMask message = HasFieldMask.newBuilder().setFieldMask(FIELD_MASK).build();
    String json = camelCase(Include.ALWAYS).writeValueAsString(message);
    assertThat(json).isEqualTo("{\"fieldMask\":\"pathOne,pathTwo\"}");
  }

  @Test
  public void itWritesEmptyStringWhenSetToDefaultInstanceWithAlwaysInclusion() {
    HasFieldMask message = HasFieldMask
      .newBuilder()
      .setFieldMask(FieldMask.getDefaultInstance())
      .build();
    String json = camelCase(Include.ALWAYS).writeValueAsString(message);
    assertThat(json).isEqualTo("{\"fieldMask\":\"\"}");
  }

  @Test
  public void itWritesNullWhenNotSetWithAlwaysInclusion() {
    HasFieldMask message = HasFieldMask.newBuilder().build();
    String json = camelCase(Include.ALWAYS).writeValueAsString(message);
    assertThat(json).isEqualTo("{\"fieldMask\":null}");
  }

  @Test
  public void itWritesFieldMaskWhenSetWithNonNullInclusion() {
    HasFieldMask message = HasFieldMask.newBuilder().setFieldMask(FIELD_MASK).build();
    String json = camelCase(Include.NON_NULL).writeValueAsString(message);
    assertThat(json).isEqualTo("{\"fieldMask\":\"pathOne,pathTwo\"}");
  }

  @Test
  public void itWritesEmptyStringWhenSetToDefaultInstanceWithNonNullInclusion() {
    HasFieldMask message = HasFieldMask
      .newBuilder()
      .setFieldMask(FieldMask.getDefaultInstance())
      .build();
    String json = camelCase(Include.NON_NULL).writeValueAsString(message);
    assertThat(json).isEqualTo("{\"fieldMask\":\"\"}");
  }

  @Test
  public void itOmitsFieldMaskWhenNotSetWithNonNullInclusion() {
    HasFieldMask message = HasFieldMask.newBuilder().build();
    String json = camelCase(Include.NON_NULL).writeValueAsString(message);
    assertThat(json).isEqualTo("{}");
  }

  @Test
  public void itSetsFieldMaskWhenPresentInJson() {
    String json = "{\"fieldMask\":\"pathOne,pathTwo\"}";
    HasFieldMask message = camelCase().readValue(json, HasFieldMask.class);
    assertThat(message.hasFieldMask()).isTrue();
    assertThat(message.getFieldMask()).isEqualTo(FIELD_MASK);
  }

  @Test
  public void itSetsFieldMaskWhenEmptyInJson() {
    String json = "{\"fieldMask\":\"\"}";
    HasFieldMask message = camelCase().readValue(json, HasFieldMask.class);
    assertThat(message.hasFieldMask()).isTrue();
    assertThat(message.getFieldMask()).isEqualTo(FieldMask.getDefaultInstance());
  }

  @Test
  public void itDoesntSetFieldMaskWhenNullInJson() {
    String json = "{\"fieldMask\":null}";
    HasFieldMask message = camelCase().readValue(json, HasFieldMask.class);
    assertThat(message.hasFieldMask()).isFalse();
  }

  @Test
  public void itDoesntSetFieldMaskWhenMissingFromJson() {
    String json = "{}";
    HasFieldMask message = camelCase().readValue(json, HasFieldMask.class);
    assertThat(message.hasFieldMask()).isFalse();
  }
}
