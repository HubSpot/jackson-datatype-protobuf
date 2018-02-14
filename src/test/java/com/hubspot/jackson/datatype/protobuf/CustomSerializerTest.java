package com.hubspot.jackson.datatype.protobuf;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.protobuf.Message;
import com.hubspot.jackson.datatype.protobuf.util.TestProtobuf.AllFields;
import com.hubspot.jackson.datatype.protobuf.util.TestProtobuf.Nested;
import com.hubspot.jackson.datatype.protobuf.util.TestProtobuf3.Custom;
import com.hubspot.jackson.datatype.protobuf.util.TestProtobuf3.CustomMessageWrapper;
import com.hubspot.jackson.datatype.protobuf.util.TestProtobuf3.RepeatedCustomWrapper;

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

  @Test
  public void itUsesCustomSerializerForTopLevelObject() throws IOException {
    ObjectMapper mapper = new ObjectMapper().registerModules(new ProtobufModule(), new CustomSerializer());

    Custom custom = Custom.newBuilder().setValue(123).build();
    String json = mapper.writeValueAsString(custom);
    assertThat(json).isEqualTo("123");
  }

  @Test
  public void itUsesCustomSerializerForWrappedObject() throws IOException {
    ObjectMapper mapper = new ObjectMapper().registerModules(new ProtobufModule(), new CustomSerializer());

    Custom custom = Custom.newBuilder().setValue(123).build();
    CustomMessageWrapper wrapper = CustomMessageWrapper.newBuilder().setCustom(custom).build();
    String json = mapper.writeValueAsString(wrapper);
    assertThat(json).isEqualTo("{\"custom\":123}");
  }

  @Test
  public void itUsesCustomSerializerForWrappedRepeatedObject() throws IOException {
    ObjectMapper mapper = new ObjectMapper().registerModules(new ProtobufModule(), new CustomSerializer());

    Custom first = Custom.newBuilder().setValue(123).build();
    Custom second = Custom.newBuilder().setValue(456).build();
    RepeatedCustomWrapper wrapper = RepeatedCustomWrapper.newBuilder().addCustom(first).addCustom(second).build();
    String json = mapper.writeValueAsString(wrapper);
    assertThat(json).isEqualTo("{\"custom\":[123,456]}");
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

  public static class CustomSerializer extends SimpleModule {

    public CustomSerializer() {
      addSerializer(Custom.class, new JsonSerializer<Custom>() {

        @Override
        public void serialize(Custom custom, JsonGenerator jgen, SerializerProvider provider) throws IOException {
          jgen.writeNumber(custom.getValue());
        }
      });
    }
  }
}
