package com.hubspot.jackson.datatype.protobuf;

import static com.hubspot.jackson.datatype.protobuf.util.ObjectMapperHelper.camelCase;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hubspot.jackson.datatype.protobuf.util.TestProtobuf.AllFields;

public class EnumProcessingHookTest {

  @Test
  public void itCallsTheHook() throws Exception {
    final AtomicInteger count = new AtomicInteger();
    ProtobufJacksonConfig config = ProtobufJacksonConfig.builder().addEnumClassProcessingHook(clazz -> count.incrementAndGet()).build();
    ObjectMapper mapper = new ObjectMapper().registerModules(new ProtobufModule(config));

    mapper.treeToValue(camelCase().createObjectNode().put("enum", "ONE"), AllFields.class);
    assertThat(count.get()).isEqualTo(1);
  }
}
