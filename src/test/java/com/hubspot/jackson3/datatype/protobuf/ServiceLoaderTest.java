package com.hubspot.jackson3.datatype.protobuf;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import tools.jackson.databind.cfg.MapperBuilder;

public class ServiceLoaderTest {

  @Test
  public void itRegistersProtobufModule() {
    long protobufModules = MapperBuilder
      .findModules()
      .stream()
      .filter(module -> module instanceof ProtobufModule)
      .count();

    assertThat(protobufModules)
      .describedAs("Check ProtobufModule registered")
      .isEqualTo(1);
  }
}
