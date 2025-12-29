package com.hubspot.jackson.datatype.protobuf.builtin;

import static com.hubspot.jackson.datatype.protobuf.util.ObjectMapperHelper.camelCase;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.protobuf.ListValue;
import com.google.protobuf.NullValue;
import com.google.protobuf.Struct;
import com.google.protobuf.Value;
import com.hubspot.jackson.datatype.protobuf.util.BuiltInProtobufs.RepeatedValue;
import org.junit.Test;

public class RepeatedValueTest {

  private static final Value NESTED = Value.newBuilder().setStringValue("nested").build();
  private static final Value LIST = Value
    .newBuilder()
    .setListValue(ListValue.newBuilder().addValues(NESTED).build())
    .build();
  private static final Struct STRUCT = Struct
    .newBuilder()
    .putFields("key", Value.newBuilder().setStringValue("value").build())
    .build();

  @Test
  public void itReadsListValues() {
    String json = "{\"values\":[\"nested\"]}";
    RepeatedValue message = camelCase().readValue(json, RepeatedValue.class);
    assertThat(message.getValuesCount()).isEqualTo(1);
    assertThat(message.getValues(0)).isEqualTo(NESTED);
  }

  @Test
  public void itReadsNestedListValues() {
    String json = "{\"values\":[[\"nested\"]]}";
    RepeatedValue message = camelCase().readValue(json, RepeatedValue.class);
    assertThat(message.getValuesCount()).isEqualTo(1);
    assertThat(message.getValues(0)).isEqualTo(LIST);
  }

  @Test
  public void itReadsMixedTypeValues() {
    String json =
      "{\"values\":[null,1.5,\"test\",true,{\"key\":\"value\"},[\"nested\"]]}";
    RepeatedValue message = camelCase().readValue(json, RepeatedValue.class);
    assertThat(message.getValuesCount()).isEqualTo(6);
    assertThat(message.getValues(0))
      .isEqualTo(Value.newBuilder().setNullValue(NullValue.NULL_VALUE).build());
    assertThat(message.getValues(1))
      .isEqualTo(Value.newBuilder().setNumberValue(1.5d).build());
    assertThat(message.getValues(2))
      .isEqualTo(Value.newBuilder().setStringValue("test").build());
    assertThat(message.getValues(3))
      .isEqualTo(Value.newBuilder().setBoolValue(true).build());
    assertThat(message.getValues(4))
      .isEqualTo(Value.newBuilder().setStructValue(STRUCT).build());
    assertThat(message.getValues(5)).isEqualTo(LIST);
  }
}
