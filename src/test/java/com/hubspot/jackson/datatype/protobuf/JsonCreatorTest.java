package com.hubspot.jackson.datatype.protobuf;

import static com.hubspot.jackson.datatype.protobuf.util.ObjectMapperHelper.camelCase;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.hubspot.jackson.datatype.protobuf.util.ProtobufCreator;
import com.hubspot.jackson.datatype.protobuf.util.TestProtobuf.AllFields;
import org.junit.Test;

public class JsonCreatorTest {

  @Test
  public void testEmptyObject() {
    Wrapper wrapper = camelCase().readValue("{}", Wrapper.class);

    assertThat(wrapper.getAllFields()).isEqualTo(AllFields.getDefaultInstance());
  }

  @Test
  public void testPopulatedObject() {
    AllFields original = ProtobufCreator.create(AllFields.class);

    Wrapper wrapper = camelCase()
      .readValue(camelCase().writeValueAsString(original), Wrapper.class);

    assertThat(wrapper.getAllFields()).isEqualTo(original);
  }

  private static class Wrapper {

    private final AllFields allFields;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public Wrapper(AllFields allFields) {
      this.allFields = allFields;
    }

    public AllFields getAllFields() {
      return allFields;
    }
  }
}
