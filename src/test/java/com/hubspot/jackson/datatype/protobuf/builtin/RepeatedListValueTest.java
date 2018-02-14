package com.hubspot.jackson.datatype.protobuf.builtin;

import static com.hubspot.jackson.datatype.protobuf.util.ObjectMapperHelper.camelCase;

import java.io.IOException;

import org.junit.Test;

import com.hubspot.jackson.datatype.protobuf.util.BuiltInProtobufs.RepeatedListValue;

public class RepeatedListValueTest {

  @Test
  public void itReadsNestedListValues() throws IOException {
    String json = "{\"listValues\":[[\"nested\"]]}";
    RepeatedListValue message = camelCase().readValue(json, RepeatedListValue.class);
  }

  @Test
  public void itReadsMixedTypeValues() throws IOException {
    String json = "{\"listValues\":[null,1.5,\"test\",true,{\"key\":\"value\"},[\"nested\"]]}";
    RepeatedListValue message = camelCase().readValue(json, RepeatedListValue.class);
  }
}
