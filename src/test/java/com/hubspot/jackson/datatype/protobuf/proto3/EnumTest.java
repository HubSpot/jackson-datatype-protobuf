package com.hubspot.jackson.datatype.protobuf.proto3;

import static com.hubspot.jackson.datatype.protobuf.UnknownEnumSerializationStrategy.FAIL;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hubspot.jackson.datatype.protobuf.ProtobufJacksonConfig;
import com.hubspot.jackson.datatype.protobuf.ProtobufModule;
import com.hubspot.jackson.datatype.protobuf.util.TestProtobuf3.NestedProto3;
import org.junit.Test;

public class EnumTest {

  @Test
  public void itFailsToSerializeAnUnknownEnum() {
    ProtobufJacksonConfig config = ProtobufJacksonConfig.builder()
        .unknownEnumSerializationStrategy(FAIL)
        .build();

    ObjectMapper mapper = new ObjectMapper().registerModules(new ProtobufModule(config));

    NestedProto3 message = NestedProto3.newBuilder()
        .setEnumValue(42)
        .build();

    assertThatThrownBy(() -> mapper.writeValueAsString(message))
        .hasMessageContaining("Unable to serialize an unknown enum value")
        .hasRootCauseInstanceOf(IllegalArgumentException.class);
  }
}
