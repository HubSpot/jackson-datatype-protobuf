package com.hubspot.jackson.datatype.protobuf;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.protobuf.Message;
import com.hubspot.jackson.datatype.protobuf.util.TestProtobuf.AllFields;
import com.hubspot.jackson.datatype.protobuf.util.TestProtobuf.Nested;
import com.hubspot.jackson.datatype.protobuf.util.TestProtobuf3.Custom;
import com.hubspot.jackson.datatype.protobuf.util.TestProtobuf3.CustomMessageWrapper;
import com.hubspot.jackson.datatype.protobuf.util.TestProtobuf3.RepeatedCustomWrapper;

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

  @Test
  public void itUsesCustomDeserializerForTopLevelObject() throws IOException {
    ObjectMapper mapper = new ObjectMapper().registerModules(new ProtobufModule(), new CustomDeserializer());

    String json = "123";
    Custom custom = mapper.readValue(json, Custom.class);
    assertThat(custom).isEqualTo(Custom.newBuilder().setValue(123).build());
  }

  @Test
  public void itUsesCustomDeserializerForWrappedObject() throws IOException {
    ObjectMapper mapper = new ObjectMapper().registerModules(new ProtobufModule(), new CustomDeserializer());

    String json = "{\"custom\":123}";
    CustomMessageWrapper wrapper = mapper.readValue(json, CustomMessageWrapper.class);
    assertThat(wrapper.hasCustom()).isTrue();
    assertThat(wrapper.getCustom()).isEqualTo(Custom.newBuilder().setValue(123).build());
  }

  @Test
  public void itUsesCustomDeserializerForWrappedRepeatedObject() throws IOException {
    ObjectMapper mapper = new ObjectMapper().registerModules(new ProtobufModule(), new CustomDeserializer());

    String json = "{\"custom\":[123,456]}";
    RepeatedCustomWrapper wrapper = mapper.readValue(json, RepeatedCustomWrapper.class);
    assertThat(wrapper.getCustomCount()).isEqualTo(2);
    assertThat(wrapper.getCustom(0)).isEqualTo(Custom.newBuilder().setValue(123).build());
    assertThat(wrapper.getCustom(1)).isEqualTo(Custom.newBuilder().setValue(456).build());
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

  public static class CustomDeserializer extends SimpleModule {

    public CustomDeserializer() {
      addDeserializer(Custom.class, new JsonDeserializer<Custom>() {

        @Override
        public Custom deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
          assertThat(p.getCurrentToken()).isEqualTo(JsonToken.VALUE_NUMBER_INT);
          return Custom.newBuilder().setValue(p.getIntValue()).build();
        }
      });
    }
  }
}
