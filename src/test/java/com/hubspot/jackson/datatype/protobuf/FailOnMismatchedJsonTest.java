package com.hubspot.jackson.datatype.protobuf;

import static com.hubspot.jackson.datatype.protobuf.util.ObjectMapperHelper.camelCase;

import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.hubspot.jackson.datatype.protobuf.util.BuiltInProtobufs.HasTimestamp;
import com.hubspot.jackson.datatype.protobuf.util.TestProtobuf3.AllFieldsProto3;

public class FailOnMismatchedJsonTest {

  @Test(expected = JsonMappingException.class)
  public void itFailsOnJsonArrayForNonRepeatedPrimitive() throws IOException {
    String json = "{\"double\":[1.5]}";
    camelCase().readValue(json, AllFieldsProto3.class);
  }

  @Test(expected = JsonMappingException.class)
  public void itFailsOnJsonArrayForNonRepeatedMessage() throws IOException {
    String json = "{\"nested\":[{}]}";
    camelCase().readValue(json, AllFieldsProto3.class);
  }

  @Test(expected = JsonMappingException.class)
  public void itFailsOnJsonArrayForNonRepeatedTimestamp() throws IOException {
    String json = "{\"timestamp\":[\"2000-01-01T00:00:00Z\"]}";
    camelCase().readValue(json, HasTimestamp.class);
  }
}
