package com.hubspot.jackson.datatype.protobuf.builtin;

import static com.hubspot.jackson.datatype.protobuf.util.ObjectMapperHelper.camelCase;

import java.io.IOException;

import org.junit.Test;

import com.hubspot.jackson.datatype.protobuf.util.BuiltInProtobufs.RepeatedValue;

public class RepeatedValueTest {

  @Test
  public void itReadsListValues() throws IOException {
    String json = "{\"values\":[\"nested\"]}";
    RepeatedValue test = camelCase().readValue(json, RepeatedValue.class);
  }

  @Test
  public void itReadsNestedListValues() throws IOException {
    String json = "{\"values\":[[\"nested\"]]}";
    RepeatedValue test = camelCase().readValue(json, RepeatedValue.class);
  }

  @Test
  public void itReadsMixedTypeValues() throws IOException {
    String json = "{\"values\":[null,1.5,\"test\",true,{\"key\":\"value\"},[\"nested\"]]}";
    RepeatedValue test = camelCase().readValue(json, RepeatedValue.class);
  }
}
