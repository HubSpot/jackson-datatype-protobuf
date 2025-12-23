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
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;

public class CustomDeserializerTest {

  @Test
  public void testTopLevelMessage() throws IOException {
    AllFields expected = AllFields.newBuilder().setString("test").build();
    ObjectMapper MAPPER = JsonMapper
      .builder()
      .addModules(new ProtobufModule(), new DeserializerModule(expected))
      .disable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)
      .build();

    AllFields actual = MAPPER.readValue("{}", AllFields.class);

    assertThat(actual).isEqualTo(expected);
  }

  @Test
  public void testNestedMessage() throws IOException {
    Nested expected = Nested.newBuilder().setString("test").build();
    ObjectMapper MAPPER = JsonMapper
      .builder()
      .addModules(new ProtobufModule(), new DeserializerModule(expected))
      .disable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)
      .build();

    Nested actual = MAPPER.readValue("{\"nested\":{}}", AllFields.class).getNested();

    assertThat(actual).isEqualTo(expected);
  }

  @Test
  public void itUsesCustomDeserializerForTopLevelObject() throws IOException {
    ObjectMapper mapper = JsonMapper
      .builder()
      .addModules(new ProtobufModule(), new CustomDeserializer())
      .build();

    String json = "123";
    Custom custom = mapper.readValue(json, Custom.class);
    assertThat(custom).isEqualTo(Custom.newBuilder().setValue(123).build());
  }

  @Test
  public void itUsesCustomDeserializerForWrappedObject() throws IOException {
    ObjectMapper mapper = JsonMapper
      .builder()
      .addModules(new ProtobufModule(), new CustomDeserializer())
      .build();

    String json = "{\"custom\":123}";
    CustomMessageWrapper wrapper = mapper.readValue(json, CustomMessageWrapper.class);
    assertThat(wrapper.hasCustom()).isTrue();
    assertThat(wrapper.getCustom()).isEqualTo(Custom.newBuilder().setValue(123).build());
  }

  @Test
  public void itUsesCustomDeserializerForWrappedRepeatedObject() throws IOException {
    ObjectMapper mapper = JsonMapper
      .builder()
      .addModules(new ProtobufModule(), new CustomDeserializer())
      .build();

    String json = "{\"custom\":[123,456]}";
    RepeatedCustomWrapper wrapper = mapper.readValue(json, RepeatedCustomWrapper.class);
    assertThat(wrapper.getCustomCount()).isEqualTo(2);
    assertThat(wrapper.getCustom(0)).isEqualTo(Custom.newBuilder().setValue(123).build());
    assertThat(wrapper.getCustom(1)).isEqualTo(Custom.newBuilder().setValue(456).build());
  }

  public static class DeserializerModule extends SimpleModule {

    private static final long serialVersionUID = 1L;

    @SuppressWarnings("unchecked")
    public DeserializerModule(final Message message) {
      addDeserializer(
        (Class<Message>) message.getClass(),
        new ValueDeserializer<Message>() {
          @Override
          public Message deserialize(JsonParser jp, DeserializationContext ctxt)
            throws JacksonException {
            return message;
          }
        }
      );
    }
  }

  public static class CustomDeserializer extends SimpleModule {

    private static final long serialVersionUID = 1L;

    public CustomDeserializer() {
      addDeserializer(
        Custom.class,
        new ValueDeserializer<Custom>() {
          @Override
          public Custom deserialize(JsonParser p, DeserializationContext ctxt)
            throws JacksonException {
            assertThat(p.currentToken()).isEqualTo(JsonToken.VALUE_NUMBER_INT);
            return Custom.newBuilder().setValue(p.getIntValue()).build();
          }
        }
      );
    }
  }
}
