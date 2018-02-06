package com.hubspot.jackson.datatype.protobuf;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.protobuf.Message;
import com.hubspot.jackson.datatype.protobuf.ProtobufModule;
import com.hubspot.jackson.datatype.protobuf.util.TestProtobuf.AllFields;
import com.hubspot.jackson.datatype.protobuf.util.TestProtobuf.Nested;
import org.junit.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class CustomSerializerTest {

  @Test
  public void testTopLevelMessage() {
    ObjectMapper MAPPER = new ObjectMapper().registerModules(new ProtobufModule(), new SerializerModule(AllFields.class));

    AllFields allFields = AllFields.newBuilder().setString("test").build();

    String expected = allFields.toString();
    String actual = MAPPER.valueToTree(allFields).get("toString").textValue();

    assertThat(actual).isEqualTo(expected);
  }

  @Test
  public void testNestedMessage() {
    ObjectMapper MAPPER = new ObjectMapper().registerModules(new ProtobufModule(), new SerializerModule(Nested.class));

    AllFields allFields = AllFields.newBuilder().setNested(Nested.newBuilder().setString("test").build()).build();

    String expected = allFields.getNested().toString();
    String actual = MAPPER.valueToTree(allFields).get("nested").get("toString").textValue();

    assertThat(actual).isEqualTo(expected);
  }

  public static class SerializerModule extends SimpleModule {

    public SerializerModule(Class<? extends Message> messageType) {
      addSerializer(messageType, new JsonSerializer<Message>() {

        @Override
        public void serialize(Message value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
          jgen.writeStartObject();
          jgen.writeStringField("toString", value.toString());
          jgen.writeEndObject();
        }
      });
    }
  }
}
