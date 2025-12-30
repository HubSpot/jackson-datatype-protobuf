package com.hubspot.jackson3.datatype.protobuf;

import static com.hubspot.jackson3.datatype.protobuf.util.ObjectMapperHelper.camelCase;

import com.hubspot.jackson3.datatype.protobuf.util.BuiltInProtobufs.HasTimestamp;
import com.hubspot.jackson3.datatype.protobuf.util.TestProtobuf3.AllFieldsProto3;
import org.junit.Test;
import tools.jackson.core.JacksonException;

public class FailOnMismatchedJsonTest {

  @Test(expected = JacksonException.class)
  public void itFailsOnJsonArrayForNonRepeatedPrimitive() {
    String json = "{\"double\":[1.5]}";
    camelCase().readValue(json, AllFieldsProto3.class);
  }

  @Test(expected = JacksonException.class)
  public void itFailsOnJsonArrayForNonRepeatedMessage() {
    String json = "{\"nested\":[{}]}";
    camelCase().readValue(json, AllFieldsProto3.class);
  }

  @Test(expected = JacksonException.class)
  public void itFailsOnJsonArrayForNonRepeatedTimestamp() {
    String json = "{\"timestamp\":[\"2000-01-01T00:00:00Z\"]}";
    camelCase().readValue(json, HasTimestamp.class);
  }
}
