package com.hubspot.jackson.test;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.hubspot.jackson.test.util.ProtobufCreator;
import com.hubspot.jackson.test.util.TestProtobuf.AllFields;
import org.junit.Test;

import static com.hubspot.jackson.test.util.ObjectMapperHelper.camelCase;
import static org.assertj.core.api.Assertions.assertThat;

public class JsonCreatorTest {

  @Test
  public void testEmptyObject() throws Exception {
    Wrapper wrapper = camelCase().readValue("{}", Wrapper.class);

    assertThat(wrapper.getAllFields()).isEqualTo(AllFields.getDefaultInstance());
  }

  @Test
  public void testPopulatedObject() throws Exception {
    AllFields original = ProtobufCreator.create(AllFields.class);

    Wrapper wrapper = camelCase().readValue(camelCase().writeValueAsString(original), Wrapper.class);

    assertThat(wrapper.getAllFields()).isEqualTo(original);
  }

  private static class Wrapper {
    private final AllFields allFields;

    @JsonCreator
    public Wrapper(AllFields allFields) {
      this.allFields = allFields;
    }

    public AllFields getAllFields() {
      return allFields;
    }
  }
}
