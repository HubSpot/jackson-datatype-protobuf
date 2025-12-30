package com.hubspot.jackson3.datatype.protobuf;

import static com.hubspot.jackson3.datatype.protobuf.util.ObjectMapperHelper.create;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.protobuf.Message;
import com.hubspot.jackson3.datatype.protobuf.util.TestProtobuf.AllFields;
import com.hubspot.jackson3.datatype.protobuf.util.TestProtobuf.Nested;
import com.hubspot.jackson3.datatype.protobuf.util.TestProtobuf3.Custom;
import com.hubspot.jackson3.datatype.protobuf.util.TestProtobuf3.CustomMessageWrapper;
import com.hubspot.jackson3.datatype.protobuf.util.TestProtobuf3.RepeatedCustomWrapper;
import org.junit.Test;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.module.SimpleModule;

public class CustomSerializerTest {

  @Test
  public void testTopLevelMessage() {
    ObjectMapper MAPPER = create()
      .addModule(new SerializerModule(AllFields.class))
      .build();

    AllFields allFields = AllFields.newBuilder().setString("test").build();

    String expected = allFields.toString();
    String actual = MAPPER.valueToTree(allFields).get("toString").textValue();

    assertThat(actual).isEqualTo(expected);
  }

  @Test
  public void testNestedMessage() {
    ObjectMapper MAPPER = create().addModule(new SerializerModule(Nested.class)).build();

    AllFields allFields = AllFields
      .newBuilder()
      .setNested(Nested.newBuilder().setString("test").build())
      .build();

    String expected = allFields.getNested().toString();
    String actual = MAPPER
      .valueToTree(allFields)
      .get("nested")
      .get("toString")
      .textValue();

    assertThat(actual).isEqualTo(expected);
  }

  @Test
  public void itUsesCustomSerializerForTopLevelObject() {
    ObjectMapper mapper = create().addModule(new CustomSerializer()).build();

    Custom custom = Custom.newBuilder().setValue(123).build();
    String json = mapper.writeValueAsString(custom);
    assertThat(json).isEqualTo("123");
  }

  @Test
  public void itUsesCustomSerializerForWrappedObject() {
    ObjectMapper mapper = create().addModule(new CustomSerializer()).build();

    Custom custom = Custom.newBuilder().setValue(123).build();
    CustomMessageWrapper wrapper = CustomMessageWrapper
      .newBuilder()
      .setCustom(custom)
      .build();
    String json = mapper.writeValueAsString(wrapper);
    assertThat(json).isEqualTo("{\"custom\":123}");
  }

  @Test
  public void itUsesCustomSerializerForWrappedRepeatedObject() {
    ObjectMapper mapper = create().addModule(new CustomSerializer()).build();

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

    public SerializerModule(Class<? extends Message> messageType) {
      addSerializer(
        messageType,
        new ValueSerializer<Message>() {
          @Override
          public void serialize(
            Message value,
            JsonGenerator jgen,
            SerializationContext serializationContext
          ) {
            jgen.writeStartObject();
            jgen.writeStringProperty("toString", value.toString());
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
            SerializationContext serializationContext
          ) {
            jgen.writeNumber(custom.getValue());
          }
        }
      );
    }
  }
}
