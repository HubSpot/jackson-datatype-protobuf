package com.hubspot.jackson.datatype.protobuf.builtin;

import static com.hubspot.jackson.datatype.protobuf.util.ObjectMapperHelper.camelCase;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.Test;

import com.google.protobuf.ListValue;
import com.google.protobuf.Value;
import com.hubspot.jackson.datatype.protobuf.util.BuiltInProtobufs.RepeatedListValue;

public class RepeatedListValueTest {
  private static final Value NESTED = Value.newBuilder().setStringValue("nested").build();
  private static final ListValue LIST = ListValue
          .newBuilder()
          .addValues(NESTED)
          .build();

  @Test
  public void itReadsNestedListValues() throws IOException {
    String json = "{\"listValues\":[[\"nested\"]]}";
    RepeatedListValue message = camelCase().readValue(json, RepeatedListValue.class);
    assertThat(message.getListValuesCount()).isEqualTo(1);
    assertThat(message.getListValues(0)).isEqualTo(LIST);
  }
}
