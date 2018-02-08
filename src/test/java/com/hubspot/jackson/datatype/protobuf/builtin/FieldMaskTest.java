package com.hubspot.jackson.datatype.protobuf.builtin;

import static com.hubspot.jackson.datatype.protobuf.util.ObjectMapperHelper.camelCase;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.google.protobuf.FieldMask;
import com.hubspot.jackson.datatype.protobuf.util.BuiltInProtobufs.HasFieldMask;

public class FieldMaskTest {
  private static final FieldMask FIELD_MASK = FieldMask.newBuilder().addPaths("path_one").addPaths("path_two").build();

  @Test
  public void itWritesFieldMaskWhenSetWithDefaultInclusion() throws IOException {
    HasFieldMask message = HasFieldMask.newBuilder().setFieldMask(FIELD_MASK).build();
    String json = camelCase().writeValueAsString(message);
    assertThat(json).isEqualTo("{\"fieldMask\":\"pathOne,pathTwo\"}");
  }

  @Test
  public void itWritesEmptyStringWhenSetToDefaultInstanceWithDefaultInclusion() throws IOException {
    HasFieldMask message = HasFieldMask.newBuilder().setFieldMask(FieldMask.getDefaultInstance()).build();
    String json = camelCase().writeValueAsString(message);
    assertThat(json).isEqualTo("{\"fieldMask\":\"\"}");
  }

  @Test
  public void itOmitsFieldMaskWhenNotSetWithDefaultInclusion() throws IOException {
    HasFieldMask message = HasFieldMask.newBuilder().build();
    String json = camelCase().writeValueAsString(message);
    assertThat(json).isEqualTo("{}");
  }

  @Test
  public void itWritesFieldMaskWhenSetWithNonDefaultInclusion() throws IOException {
    HasFieldMask message = HasFieldMask.newBuilder().setFieldMask(FIELD_MASK).build();
    String json = camelCase(Include.NON_DEFAULT).writeValueAsString(message);
    assertThat(json).isEqualTo("{\"fieldMask\":\"pathOne,pathTwo\"}");
  }

  @Test
  public void itWritesEmptyStringWhenSetToDefaultInstanceWithNonDefaultInclusion() throws IOException {
    HasFieldMask message = HasFieldMask.newBuilder().setFieldMask(FieldMask.getDefaultInstance()).build();
    String json = camelCase(Include.NON_DEFAULT).writeValueAsString(message);
    assertThat(json).isEqualTo("{\"fieldMask\":\"\"}");
  }

  @Test
  public void itOmitsFieldMaskWhenNotSetWithNonDefaultInclusion() throws IOException {
    HasFieldMask message = HasFieldMask.newBuilder().build();
    String json = camelCase(Include.NON_DEFAULT).writeValueAsString(message);
    assertThat(json).isEqualTo("{}");
  }

  @Test
  public void itWritesFieldMaskSetWithAlwaysInclusion() throws IOException {
    HasFieldMask message = HasFieldMask.newBuilder().setFieldMask(FIELD_MASK).build();
    String json = camelCase(Include.ALWAYS).writeValueAsString(message);
    assertThat(json).isEqualTo("{\"fieldMask\":\"pathOne,pathTwo\"}");
  }

  @Test
  public void itWritesEmptyStringWhenSetToDefaultInstanceWithAlwaysInclusion() throws IOException {
    HasFieldMask message = HasFieldMask.newBuilder().setFieldMask(FieldMask.getDefaultInstance()).build();
    String json = camelCase(Include.ALWAYS).writeValueAsString(message);
    assertThat(json).isEqualTo("{\"fieldMask\":\"\"}");
  }

  @Test
  public void itWritesNullWhenNotSetWithAlwaysInclusion() throws IOException {
    HasFieldMask message = HasFieldMask.newBuilder().build();
    String json = camelCase(Include.ALWAYS).writeValueAsString(message);
    assertThat(json).isEqualTo("{\"fieldMask\":null}");
  }

  @Test
  public void itWritesFieldMaskWhenSetWithNonNullInclusion() throws IOException {
    HasFieldMask message = HasFieldMask.newBuilder().setFieldMask(FIELD_MASK).build();
    String json = camelCase(Include.NON_NULL).writeValueAsString(message);
    assertThat(json).isEqualTo("{\"fieldMask\":\"pathOne,pathTwo\"}");
  }

  @Test
  public void itWritesEmptyStringWhenSetToDefaultInstanceWithNonNullInclusion() throws IOException {
    HasFieldMask message = HasFieldMask.newBuilder().setFieldMask(FieldMask.getDefaultInstance()).build();
    String json = camelCase(Include.NON_NULL).writeValueAsString(message);
    assertThat(json).isEqualTo("{\"fieldMask\":\"\"}");
  }

  @Test
  public void itOmitsFieldMaskWhenNotSetWithNonNullInclusion() throws IOException {
    HasFieldMask message = HasFieldMask.newBuilder().build();
    String json = camelCase(Include.NON_NULL).writeValueAsString(message);
    assertThat(json).isEqualTo("{}");
  }

  @Test
  public void itSetsFieldMaskWhenPresentInJson() throws IOException {
    String json = "{\"fieldMask\":\"pathOne,pathTwo\"}";
    HasFieldMask message = camelCase().readValue(json, HasFieldMask.class);
    assertThat(message.hasFieldMask()).isTrue();
    assertThat(message.getFieldMask()).isEqualTo(FIELD_MASK);
  }

  @Test
  public void itSetsFieldMaskWhenEmptyInJson() throws IOException {
    String json = "{\"fieldMask\":\"\"}";
    HasFieldMask message = camelCase().readValue(json, HasFieldMask.class);
    assertThat(message.hasFieldMask()).isTrue();
    assertThat(message.getFieldMask()).isEqualTo(FieldMask.getDefaultInstance());
  }

  @Test
  public void itDoesntSetFieldMaskWhenNullInJson() throws IOException {
    String json = "{\"fieldMask\":null}";
    HasFieldMask message = camelCase().readValue(json, HasFieldMask.class);
    assertThat(message.hasFieldMask()).isFalse();
  }

  @Test
  public void itDoesntSetFieldMaskWhenMissingFromJson() throws IOException {
    String json = "{}";
    HasFieldMask message = camelCase().readValue(json, HasFieldMask.class);
    assertThat(message.hasFieldMask()).isFalse();
  }
}
