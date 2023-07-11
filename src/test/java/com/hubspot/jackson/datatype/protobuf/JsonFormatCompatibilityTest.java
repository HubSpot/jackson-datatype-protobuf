package com.hubspot.jackson.datatype.protobuf;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.util.JsonFormat;
import com.hubspot.jackson.datatype.protobuf.util.ProtobufCreator;
import com.hubspot.jackson.datatype.protobuf.util.TestProtobuf.AllFields;
import com.hubspot.jackson.datatype.protobuf.util.TestProtobuf3.AllFieldsProto3;
import java.io.IOException;
import org.junit.Test;

public class JsonFormatCompatibilityTest {
  private static final ObjectMapper MAPPER = new ObjectMapper().registerModule(
      new ProtobufModule(ProtobufJacksonConfig.builder().useCanonicalSerialization().build())
  );

  @Test
  public void weSerializeAndJsonFormatDeserializesProto2() throws IOException {
    repeat(() -> {
      AllFields original = ProtobufCreator.create(AllFields.class);
      String json = MAPPER.writeValueAsString(original);

      AllFields.Builder builder = AllFields.newBuilder();
      JsonFormat.parser().merge(json, builder);

      assertThat(builder.build()).isEqualTo(original);
    }, 1_000);
  }

  @Test
  public void jsonFormatSerializesAndWeDeserializeProto2() throws IOException {
    repeat(() -> {
      AllFields original = ProtobufCreator.create(AllFields.class);
      String json = JsonFormat.printer().print(original);

      AllFields parsed = MAPPER.readValue(json, AllFields.class);

      assertThat(parsed).isEqualTo(original);
    }, 1_000);
  }

  @Test
  public void weSerializeAndJsonFormatDeserializesProto3() throws IOException {
    repeat(() -> {
      AllFieldsProto3 original = ProtobufCreator.create(AllFieldsProto3.class);
      String json = MAPPER.writeValueAsString(original);

      AllFieldsProto3.Builder builder = AllFieldsProto3.newBuilder();
      JsonFormat.parser().merge(json, builder);

      assertThat(builder.build()).isEqualTo(original);
    }, 1_000);
  }

  @Test
  public void jsonFormatSerializesAndWeDeserializeProto3() throws IOException {
    repeat(() -> {
      AllFieldsProto3 original = ProtobufCreator.create(AllFieldsProto3.class);
      String json = JsonFormat.printer().print(original);

      AllFieldsProto3 parsed = MAPPER.readValue(json, AllFieldsProto3.class);

      assertThat(parsed).isEqualTo(original);
    }, 1_000);
  }

  private interface Runnable {
    void run() throws IOException;
  }

  private static void repeat(Runnable r, int times) throws IOException {
    for (int i = 0; i < times; i++) {
      r.run();
    }
  }
}
