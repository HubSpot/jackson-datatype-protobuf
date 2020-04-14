package com.hubspot.jackson.datatype.protobuf;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ServiceLoaderTest {

  @Test
  public void itRegistersProtobufModule() {
    long protobufModules = ObjectMapper
        .findModules()
        .stream()
        .filter(module -> module instanceof ProtobufModule)
        .count();

    assertThat(protobufModules)
        .describedAs("Check ProtobufModule registered")
        .isEqualTo(1);
  }
}
