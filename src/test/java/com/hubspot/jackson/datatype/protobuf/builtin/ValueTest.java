package com.hubspot.jackson.datatype.protobuf.builtin;

import static com.hubspot.jackson.datatype.protobuf.util.ObjectMapperHelper.camelCase;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.assertj.core.api.Assertions.fail;

import java.io.IOException;
import java.util.Map.Entry;

import org.junit.Test;

import com.google.protobuf.NullValue;
import com.google.protobuf.Value;
import com.hubspot.jackson.datatype.protobuf.util.BuiltInProtobufs.HasValue;

public class ValueTest {

  @Test
  public void itReadsNullValue() throws IOException {
    String json = "{\"value\":null}";
    HasValue valueWrapper = camelCase().readValue(json, HasValue.class);
    assertThat(valueWrapper.hasValue()).isTrue();

    Value value = valueWrapper.getValue();
    switch (value.getKindCase()) {
      case NULL_VALUE:
        assertThat(value.getNullValue()).isEqualTo(NullValue.NULL_VALUE);
        break;
      default:
        fail("Unexpected value kind: " + value.getKindCase());
    }
  }

  @Test
  public void itReadsIntegralValue() throws IOException {
    String json = "{\"value\":1}";
    HasValue valueWrapper = camelCase().readValue(json, HasValue.class);
    assertThat(valueWrapper.hasValue()).isTrue();

    Value value = valueWrapper.getValue();
    switch (value.getKindCase()) {
      case NUMBER_VALUE:
        assertThat(value.getNumberValue()).isEqualTo(1.0d);
        break;
      default:
        fail("Unexpected value kind: " + value.getKindCase());
    }
  }

  @Test
  public void itReadsFloatingPointValue() throws IOException {
    String json = "{\"value\":1.5}";
    HasValue valueWrapper = camelCase().readValue(json, HasValue.class);
    assertThat(valueWrapper.hasValue()).isTrue();

    Value value = valueWrapper.getValue();
    switch (value.getKindCase()) {
      case NUMBER_VALUE:
        assertThat(value.getNumberValue()).isEqualTo(1.5d);
        break;
      default:
        fail("Unexpected value kind: " + value.getKindCase());
    }
  }

  @Test
  public void itReadsStringValue() throws IOException {
    String json = "{\"value\":\"test\"}";
    HasValue valueWrapper = camelCase().readValue(json, HasValue.class);
    assertThat(valueWrapper.hasValue()).isTrue();

    Value value = valueWrapper.getValue();
    switch (value.getKindCase()) {
      case STRING_VALUE:
        assertThat(value.getStringValue()).isEqualTo("test");
        break;
      default:
        fail("Unexpected value kind: " + value.getKindCase());
    }
  }

  @Test
  public void itReadsBooleanValue() throws IOException {
    String json = "{\"value\":true}";
    HasValue valueWrapper = camelCase().readValue(json, HasValue.class);
    assertThat(valueWrapper.hasValue()).isTrue();

    Value value = valueWrapper.getValue();
    switch (value.getKindCase()) {
      case BOOL_VALUE:
        assertThat(value.getBoolValue()).isTrue();
        break;
      default:
        fail("Unexpected value kind: " + value.getKindCase());
    }
  }

  @Test
  public void itReadsStructValue() throws IOException {
    String json = "{\"value\":{\"key\":\"value\"}}";
    HasValue valueWrapper = camelCase().readValue(json, HasValue.class);
    assertThat(valueWrapper.hasValue()).isTrue();

    Value value = valueWrapper.getValue();
    switch (value.getKindCase()) {
      case STRUCT_VALUE:
        Entry<String, Value> entry = entry("key", Value.newBuilder().setStringValue("value").build());
        assertThat(value.getStructValue().getFieldsMap()).containsExactly(entry);
        break;
      default:
        fail("Unexpected value kind: " + value.getKindCase());
    }
  }

  @Test
  public void itReadsListValue() throws IOException {
    String json = "{\"value\":[]}";
    HasValue valueWrapper = camelCase().readValue(json, HasValue.class);
    assertThat(valueWrapper.hasValue()).isTrue();

    Value value = valueWrapper.getValue();
    switch (value.getKindCase()) {
      case LIST_VALUE:
        //assertThat(value.getListValue()).isTrue();
        break;
      default:
        fail("Unexpected value kind: " + value.getKindCase());
    }
  }
}
