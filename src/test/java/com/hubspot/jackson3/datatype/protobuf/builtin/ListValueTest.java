package com.hubspot.jackson3.datatype.protobuf.builtin;

import static com.hubspot.jackson3.datatype.protobuf.util.ObjectMapperHelper.camelCase;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.google.protobuf.ListValue;
import com.google.protobuf.NullValue;
import com.google.protobuf.Struct;
import com.google.protobuf.Value;
import com.hubspot.jackson3.datatype.protobuf.util.BuiltInProtobufs.HasListValue;
import org.junit.Test;

public class ListValueTest {

  private static final Value VALUE = Value.newBuilder().setStringValue("test").build();
  private static final ListValue LIST_VALUE = ListValue
    .newBuilder()
    .addValues(VALUE)
    .build();

  @Test
  public void itWritesListValueWhenSetWithDefaultInclusion() {
    HasListValue message = HasListValue.newBuilder().setListValue(LIST_VALUE).build();
    String json = camelCase().writeValueAsString(message);
    assertThat(json).isEqualTo("{\"listValue\":[\"test\"]}");
  }

  @Test
  public void itWritesEmptyArrayWhenSetToDefaultInstanceWithDefaultInclusion() {
    HasListValue message = HasListValue
      .newBuilder()
      .setListValue(ListValue.getDefaultInstance())
      .build();
    String json = camelCase().writeValueAsString(message);
    assertThat(json).isEqualTo("{\"listValue\":[]}");
  }

  @Test
  public void itOmitsListValueWhenNotSetWithDefaultInclusion() {
    HasListValue message = HasListValue.newBuilder().build();
    String json = camelCase().writeValueAsString(message);
    assertThat(json).isEqualTo("{}");
  }

  @Test
  public void itWritesListValueWhenSetWithNonDefaultInclusion() {
    HasListValue message = HasListValue.newBuilder().setListValue(LIST_VALUE).build();
    String json = camelCase(Include.NON_DEFAULT).writeValueAsString(message);
    assertThat(json).isEqualTo("{\"listValue\":[\"test\"]}");
  }

  @Test
  public void itWritesEmptyArrayWhenSetToDefaultInstanceWithNonDefaultInclusion() {
    HasListValue message = HasListValue
      .newBuilder()
      .setListValue(ListValue.getDefaultInstance())
      .build();
    String json = camelCase(Include.NON_DEFAULT).writeValueAsString(message);
    assertThat(json).isEqualTo("{\"listValue\":[]}");
  }

  @Test
  public void itOmitsListValueWhenNotSetWithNonDefaultInclusion() {
    HasListValue message = HasListValue.newBuilder().build();
    String json = camelCase(Include.NON_DEFAULT).writeValueAsString(message);
    assertThat(json).isEqualTo("{}");
  }

  @Test
  public void itWritesListValueSetWithAlwaysInclusion() {
    HasListValue message = HasListValue.newBuilder().setListValue(LIST_VALUE).build();
    String json = camelCase(Include.ALWAYS).writeValueAsString(message);
    assertThat(json).isEqualTo("{\"listValue\":[\"test\"]}");
  }

  @Test
  public void itWritesEmptyArrayWhenSetToDefaultInstanceWithAlwaysInclusion() {
    HasListValue message = HasListValue
      .newBuilder()
      .setListValue(ListValue.getDefaultInstance())
      .build();
    String json = camelCase(Include.ALWAYS).writeValueAsString(message);
    assertThat(json).isEqualTo("{\"listValue\":[]}");
  }

  @Test
  public void itWritesNullWhenNotSetWithAlwaysInclusion() {
    HasListValue message = HasListValue.newBuilder().build();
    String json = camelCase(Include.ALWAYS).writeValueAsString(message);
    assertThat(json).isEqualTo("{\"listValue\":null}");
  }

  @Test
  public void itWritesListValueWhenSetWithNonNullInclusion() {
    HasListValue message = HasListValue.newBuilder().setListValue(LIST_VALUE).build();
    String json = camelCase(Include.NON_NULL).writeValueAsString(message);
    assertThat(json).isEqualTo("{\"listValue\":[\"test\"]}");
  }

  @Test
  public void itWritesEmptyArrayWhenSetToDefaultInstanceWithNonNullInclusion() {
    HasListValue message = HasListValue
      .newBuilder()
      .setListValue(ListValue.getDefaultInstance())
      .build();
    String json = camelCase(Include.NON_NULL).writeValueAsString(message);
    assertThat(json).isEqualTo("{\"listValue\":[]}");
  }

  @Test
  public void itOmitsListValueWhenNotSetWithNonNullInclusion() {
    HasListValue message = HasListValue.newBuilder().build();
    String json = camelCase(Include.NON_NULL).writeValueAsString(message);
    assertThat(json).isEqualTo("{}");
  }

  @Test
  public void itReadsNestedListValues() {
    String json = "{\"listValue\":[[\"nested\"]]}";
    HasListValue message = camelCase().readValue(json, HasListValue.class);
    assertThat(message.hasListValue()).isTrue();
    assertThat(message.getListValue().getValuesList()).hasSize(1);
    Value value = message.getListValue().getValues(0);
    ListValue list = ListValue
      .newBuilder()
      .addValues(Value.newBuilder().setStringValue("nested"))
      .build();
    switch (value.getKindCase()) {
      case LIST_VALUE:
        assertThat(value.getListValue()).isEqualToComparingFieldByField(list);
        break;
      default:
        fail("Unexpected value kind: " + value.getKindCase());
    }
  }

  @Test
  public void itReadsMixedTypeValues() {
    String json =
      "{\"listValue\":[null,1.5,\"test\",true,{\"key\":\"value\"},[\"nested\"]]}";
    HasListValue message = camelCase().readValue(json, HasListValue.class);
    Struct struct = Struct
      .newBuilder()
      .putFields("key", Value.newBuilder().setStringValue("value").build())
      .build();
    ListValue list = ListValue
      .newBuilder()
      .addValues(Value.newBuilder().setStringValue("nested"))
      .build();
    ListValue expected = ListValue
      .newBuilder()
      .addValues(Value.newBuilder().setNullValue(NullValue.NULL_VALUE).build())
      .addValues(Value.newBuilder().setNumberValue(1.5d).build())
      .addValues(Value.newBuilder().setStringValue("test").build())
      .addValues(Value.newBuilder().setBoolValue(true).build())
      .addValues(Value.newBuilder().setStructValue(struct).build())
      .addValues(Value.newBuilder().setListValue(list).build())
      .build();
    assertThat(message.hasListValue()).isTrue();
    assertThat(message.getListValue()).isEqualTo(expected);
  }

  @Test
  public void itSetsListValueWhenPresentInJson() {
    String json = "{\"listValue\":[\"test\"]}";
    HasListValue message = camelCase().readValue(json, HasListValue.class);
    assertThat(message.hasListValue()).isTrue();
    assertThat(message.getListValue()).isEqualTo(LIST_VALUE);
  }

  @Test
  public void itSetsListValueWhenEmptyInJson() {
    String json = "{\"listValue\":[]}";
    HasListValue message = camelCase().readValue(json, HasListValue.class);
    assertThat(message.hasListValue()).isTrue();
    assertThat(message.getListValue()).isEqualTo(ListValue.getDefaultInstance());
  }

  @Test
  public void itDoesntSetListValueWhenNullInJson() {
    String json = "{\"listValue\":null}";
    HasListValue message = camelCase().readValue(json, HasListValue.class);
    assertThat(message.hasListValue()).isFalse();
  }

  @Test
  public void itDoesntSetListValueWhenMissingFromJson() {
    String json = "{}";
    HasListValue message = camelCase().readValue(json, HasListValue.class);
    assertThat(message.hasListValue()).isFalse();
  }
}
