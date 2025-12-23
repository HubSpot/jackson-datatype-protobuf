package com.hubspot.jackson.datatype.protobuf;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.protobuf.Message;
import com.hubspot.jackson.datatype.protobuf.util.TestProtobuf.AllFields;
import com.hubspot.jackson.datatype.protobuf.util.TestProtobuf.Nested;
import com.hubspot.jackson.datatype.protobuf.util.TestProtobuf3.Custom;
import com.hubspot.jackson.datatype.protobuf.util.TestProtobuf3.CustomMessageWrapper;
import com.hubspot.jackson.datatype.protobuf.util.TestProtobuf3.RepeatedCustomWrapper;
import java.io.IOException;
import org.junit.Test;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;

public class CustomSerializerTest {

  @Test
  public void testTopLevelMessage() {
    ObjectMapper MAPPER = JsonMapper
      .builder()
      .addModules(new ProtobufModule(), new SerializerModule(AllFields.class))
      .build();

    AllFields allFields = AllFields.newBuilder().setString("test").build();

    String expected = allFields.toString();
    String actual = MAPPER.valueToTree(allFields).get("toString").stringValue();

    assertThat(actual).isEqualTo(expected);
  }

  @Test
  public void testNestedMessage() {
    ObjectMapper MAPPER = JsonMapper
      .builder()
      .addModules(new ProtobufModule(), new SerializerModule(Nested.class))
      .build();

    AllFields allFields = AllFields
      .newBuilder()
      .setNested(Nested.newBuilder().setString("test").build())
      .build();

    String expected = allFields.getNested().toString();
    String actual = MAPPER
      .valueToTree(allFields)
      .get("nested")
      .get("toString")
      .stringValue();

    assertThat(actual).isEqualTo(expected);
  }

  @Test
  public void itUsesCustomSerializerForTopLevelObject() throws IOException {
    ObjectMapper mapper = JsonMapper
      .builder()
      .addModules(new ProtobufModule(), new CustomSerializer())
      .build();

    Custom custom = Custom.newBuilder().setValue(123).build();
    String json = mapper.writeValueAsString(custom);
    assertThat(json).isEqualTo("123");
  }

  @Test
  public void itUsesCustomSerializerForWrappedObject() throws IOException {
    ObjectMapper mapper = JsonMapper
      .builder()
      .addModules(new ProtobufModule(), new CustomSerializer())
      .build();

    Custom custom = Custom.newBuilder().setValue(123).build();
    CustomMessageWrapper wrapper = CustomMessageWrapper
      .newBuilder()
      .setCustom(custom)
      .build();
    String json = mapper.writeValueAsString(wrapper);
    assertThat(json).isEqualTo("{\"custom\":123}");
  }

  @Test
  public void itUsesCustomSerializerForWrappedRepeatedObject() throws IOException {
    ObjectMapper mapper = JsonMapper
      .builder()
      .addModules(new ProtobufModule(), new CustomSerializer())
      .build();

    Custom first = Custom.newBuilder().setValue(123).build();
    Custom second = Custom.newBuilder().setValue(456).build();
    RepeatedCustomWrapper wrapper = RepeatedCustomWrapper
      .newBuilder()
      .addCustom(first)
      .addCustom(second)
      .build();
    String json = mapper.writeValueAsString(wrapper);
    assertThat(json).isEqualTo("{\"custom\":[123,456]}");
  }

  public static class SerializerModule extends SimpleModule {

    private static final long serialVersionUID = 1L;

    public SerializerModule(Class<? extends Message> messageType) {
      addSerializer(
        messageType,
        new ValueSerializer<Message>() {
          @Override
          public void serialize(
            Message value,
            JsonGenerator jgen,
            SerializationContext provider
          ) throws JacksonException {
            jgen.writeStartObject();
            jgen.writeName("toString");
            jgen.writeString(value.toString());
            jgen.writeEndObject();
          }
        }
      );
    }
  }

  public static class CustomSerializer extends SimpleModule {

    public CustomSerializer() {
      addSerializer(
        Custom.class,
        new ValueSerializer<Custom>() {
          @Override
          public void serialize(
            Custom custom,
            JsonGenerator jgen,
            SerializationContext provider
          ) throws JacksonException {
            jgen.writeNumber(custom.getValue());
          }
        }
      );
    }
  }
}
