package com.hubspot.jackson.datatype.protobuf;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.protobuf.Message;
import com.hubspot.jackson.datatype.protobuf.ProtobufModule;
import com.hubspot.jackson.datatype.protobuf.util.TestProtobuf.AllFields;
import com.hubspot.jackson.datatype.protobuf.util.TestProtobuf.Nested;
import org.junit.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class CustomDeserializerTest {

  @Test
  public void testTopLevelMessage() throws IOException {
    AllFields expected = AllFields.newBuilder().setString("test").build();
    ObjectMapper MAPPER = new ObjectMapper().registerModules(new ProtobufModule(), new DeserializerModule(expected));

    AllFields actual = MAPPER.readValue("{}", AllFields.class);

    assertThat(actual).isEqualTo(expected);
  }

  @Test
  public void testNestedMessage() throws IOException {
    Nested expected = Nested.newBuilder().setString("test").build();
    ObjectMapper MAPPER = new ObjectMapper().registerModules(new ProtobufModule(), new DeserializerModule(expected));

    Nested actual = MAPPER.readValue("{\"nested\":{}}", AllFields.class).getNested();

    assertThat(actual).isEqualTo(expected);
  }

  public static class DeserializerModule extends SimpleModule {

    @SuppressWarnings("unchecked")
    public DeserializerModule(final Message message) {
      addDeserializer((Class<Message>) message.getClass(), new JsonDeserializer<Message>() {

        @Override
        public Message deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
          return message;
        }
      });
    }
  }
}
