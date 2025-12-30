package com.hubspot.jackson3.datatype.protobuf.proto3;

import static com.hubspot.jackson3.datatype.protobuf.util.ObjectMapperHelper.camelCase;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.google.protobuf.Duration;
import com.hubspot.jackson3.datatype.protobuf.util.BuiltInProtobufs.HasOneof;
import com.hubspot.jackson3.datatype.protobuf.util.TestProtobuf.AllFields;
import com.hubspot.jackson3.datatype.protobuf.util.TestProtobuf3.AllFieldsProto3;
import com.hubspot.jackson3.datatype.protobuf.util.TestProtobuf3.EnumProto3;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.Test;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.NullNode;
import tools.jackson.databind.node.ObjectNode;

public class OneofTest {

  private static final HasOneof EMPTY = HasOneof.newBuilder().build();
  private static final HasOneof STRING = HasOneof.newBuilder().setString("test").build();
  private static final HasOneof DEFAULT_DURATION = HasOneof
    .newBuilder()
    .setDuration(Duration.getDefaultInstance())
    .build();
  private static final HasOneof DURATION = HasOneof
    .newBuilder()
    .setDuration(Duration.newBuilder().setSeconds(30).build())
    .build();
  private static final HasOneof DEFAULT_ENUM = HasOneof
    .newBuilder()
    .setEnum(EnumProto3.DEFAULT)
    .build();
  private static final HasOneof ENUM = HasOneof
    .newBuilder()
    .setEnum(EnumProto3.FIRST)
    .build();
  private static final HasOneof DEFAULT_PROTO2_MESSAGE = HasOneof
    .newBuilder()
    .setProto2Message(AllFields.getDefaultInstance())
    .build();
  private static final HasOneof PROTO2_MESSAGE = HasOneof
    .newBuilder()
    .setProto2Message(AllFields.newBuilder().setString("proto2").build())
    .build();
  private static final HasOneof DEFAULT_PROTO3_MESSAGE = HasOneof
    .newBuilder()
    .setProto3Message(AllFieldsProto3.getDefaultInstance())
    .build();
  private static final HasOneof PROTO3_MESSAGE = HasOneof
    .newBuilder()
    .setProto3Message(AllFieldsProto3.newBuilder().setString("proto3").build())
    .build();

  @Test
  public void itOmitsOneofWhenNotSetWithDefaultInclusion() {
    String json = camelCase().writeValueAsString(EMPTY);
    assertThat(json).isEqualTo("{}");
  }

  @Test
  public void itWritesOneofWhenSetToStringWithDefaultInclusion() {
    String json = camelCase().writeValueAsString(STRING);
    assertThat(json).isEqualTo("{\"string\":\"test\"}");
  }

  @Test
  public void itWritesOneofWhenSetToDefaultDurationWithDefaultInclusion() {
    String json = camelCase().writeValueAsString(DEFAULT_DURATION);
    assertThat(json).isEqualTo("{\"duration\":\"0s\"}");
  }

  @Test
  public void itWritesOneofWhenSetToDurationWithDefaultInclusion() {
    String json = camelCase().writeValueAsString(DURATION);
    assertThat(json).isEqualTo("{\"duration\":\"30s\"}");
  }

  @Test
  public void itWritesOneofWhenSetToDefaultEnumWithDefaultInclusion() {
    String json = camelCase().writeValueAsString(DEFAULT_ENUM);
    assertThat(json).isEqualTo("{\"enum\":\"DEFAULT\"}");
  }

  @Test
  public void itWritesOneofWhenSetToEnumWithDefaultInclusion() {
    String json = camelCase().writeValueAsString(ENUM);
    assertThat(json).isEqualTo("{\"enum\":\"FIRST\"}");
  }

  @Test
  public void itWritesOneofWhenSetToDefaultProto2MessageWithDefaultInclusion() {
    String json = camelCase().writeValueAsString(DEFAULT_PROTO2_MESSAGE);
    assertThat(json).isEqualTo("{\"proto2Message\":{}}");
  }

  @Test
  public void itWritesOneofWhenSetToProto2MessageWithDefaultInclusion() {
    String json = camelCase().writeValueAsString(PROTO2_MESSAGE);
    assertThat(json).isEqualTo("{\"proto2Message\":{\"string\":\"proto2\"}}");
  }

  @Test
  public void itWritesOneofWhenSetToDefaultProto3MessageWithDefaultInclusion() {
    String json = camelCase().writeValueAsString(DEFAULT_PROTO3_MESSAGE);
    JsonNode node = camelCase().readTree(json).get("proto3Message");
    assertThat(node).isEqualTo(nullProto3Message().without("nested"));
  }

  @Test
  public void itWritesOneofWhenSetToProto3MessageWithDefaultInclusion() {
    String json = camelCase().writeValueAsString(PROTO3_MESSAGE);
    JsonNode node = camelCase().readTree(json).get("proto3Message");
    assertThat(node)
      .isEqualTo(nullProto3Message().put("string", "proto3").without("nested"));
  }

  @Test
  public void itOmitsOneofWhenNotSetWithAlwaysInclusion() {
    String json = camelCase(Include.ALWAYS).writeValueAsString(EMPTY);
    assertThat(json).isEqualTo("{}");
  }

  @Test
  public void itWritesOneofWhenSetToStringWithAlwaysInclusion() {
    String json = camelCase(Include.ALWAYS).writeValueAsString(STRING);
    assertThat(json).isEqualTo("{\"string\":\"test\"}");
  }

  @Test
  public void itWritesOneofWhenSetToDefaultDurationWithAlwaysInclusion() {
    String json = camelCase(Include.ALWAYS).writeValueAsString(DEFAULT_DURATION);
    assertThat(json).isEqualTo("{\"duration\":\"0s\"}");
  }

  @Test
  public void itWritesOneofWhenSetToDurationWithAlwaysInclusion() {
    String json = camelCase(Include.ALWAYS).writeValueAsString(DURATION);
    assertThat(json).isEqualTo("{\"duration\":\"30s\"}");
  }

  @Test
  public void itWritesOneofWhenSetToDefaultEnumWithAlwaysInclusion() {
    String json = camelCase(Include.ALWAYS).writeValueAsString(DEFAULT_ENUM);
    assertThat(json).isEqualTo("{\"enum\":\"DEFAULT\"}");
  }

  @Test
  public void itWritesOneofWhenSetToEnumWithAlwaysInclusion() {
    String json = camelCase(Include.ALWAYS).writeValueAsString(ENUM);
    assertThat(json).isEqualTo("{\"enum\":\"FIRST\"}");
  }

  @Test
  public void itWritesOneofWhenSetToDefaultProto2MessageWithAlwaysInclusion() {
    String json = camelCase(Include.ALWAYS).writeValueAsString(DEFAULT_PROTO2_MESSAGE);
    JsonNode node = camelCase(Include.ALWAYS).readTree(json).get("proto2Message");
    assertThat(node).isEqualTo(nullProto2Message());
  }

  @Test
  public void itWritesOneofWhenSetToProto2MessageWithAlwaysInclusion() {
    String json = camelCase(Include.ALWAYS).writeValueAsString(PROTO2_MESSAGE);
    JsonNode node = camelCase(Include.ALWAYS).readTree(json).get("proto2Message");
    assertThat(node).isEqualTo(nullProto2Message().put("string", "proto2"));
  }

  @Test
  public void itWritesOneofWhenSetToDefaultProto3MessageWithAlwaysInclusion() {
    String json = camelCase(Include.ALWAYS).writeValueAsString(DEFAULT_PROTO3_MESSAGE);
    JsonNode node = camelCase(Include.ALWAYS).readTree(json).get("proto3Message");
    assertThat(node).isEqualTo(nullProto3Message());
  }

  @Test
  public void itWritesOneofWhenSetToProto3MessageWithAlwaysInclusion() {
    String json = camelCase(Include.ALWAYS).writeValueAsString(PROTO3_MESSAGE);
    JsonNode node = camelCase(Include.ALWAYS).readTree(json).get("proto3Message");
    assertThat(node).isEqualTo(nullProto3Message().put("string", "proto3"));
  }

  @Test
  public void itDoesntSetAnythingWhenJsonIsEmpty() {
    String json = "{}";
    HasOneof message = camelCase().readValue(json, HasOneof.class);
    switch (message.getOneofCase()) {
      case ONEOF_NOT_SET:
        assertThat(message).isEqualTo(HasOneof.getDefaultInstance());
        break;
      default:
        fail("Unexpected oneof set: " + message.getOneofCase());
    }
  }

  @Test
  public void itSetsStringWhenPresentInJson() {
    String json = "{\"string\":\"test\"}";
    HasOneof message = camelCase().readValue(json, HasOneof.class);
    switch (message.getOneofCase()) {
      case STRING:
        assertThat(message.getString()).isEqualTo("test");
        break;
      default:
        fail("Unexpected oneof set: " + message.getOneofCase());
    }
  }

  @Test
  public void itDoesntSetStringWhenNullInJson() {
    String json = "{\"string\":null}";
    HasOneof message = camelCase().readValue(json, HasOneof.class);
    switch (message.getOneofCase()) {
      case ONEOF_NOT_SET:
        assertThat(message).isEqualTo(HasOneof.getDefaultInstance());
        break;
      default:
        fail("Unexpected oneof set: " + message.getOneofCase());
    }
  }

  @Test
  public void itSetsDurationWhenPresentInJson() {
    String json = "{\"duration\":\"30s\"}";
    HasOneof message = camelCase().readValue(json, HasOneof.class);
    switch (message.getOneofCase()) {
      case DURATION:
        assertThat(message.getDuration())
          .isEqualTo(Duration.newBuilder().setSeconds(30).build());
        break;
      default:
        fail("Unexpected oneof set: " + message.getOneofCase());
    }
  }

  @Test
  public void itDoesntSetDurationWhenNullInJson() {
    String json = "{\"duration\":null}";
    HasOneof message = camelCase().readValue(json, HasOneof.class);
    switch (message.getOneofCase()) {
      case ONEOF_NOT_SET:
        assertThat(message).isEqualTo(HasOneof.getDefaultInstance());
        break;
      default:
        fail("Unexpected oneof set: " + message.getOneofCase());
    }
  }

  @Test
  public void itSetsEnumWhenPresentInJson() {
    String json = "{\"enum\":\"FIRST\"}";
    HasOneof message = camelCase().readValue(json, HasOneof.class);
    switch (message.getOneofCase()) {
      case ENUM:
        assertThat(message.getEnum()).isEqualTo(EnumProto3.FIRST);
        break;
      default:
        fail("Unexpected oneof set: " + message.getOneofCase());
    }
  }

  @Test
  public void itSetsEnumWhenSetToDefaultInJson() {
    String json = "{\"enum\":\"DEFAULT\"}";
    HasOneof message = camelCase().readValue(json, HasOneof.class);
    switch (message.getOneofCase()) {
      case ENUM:
        assertThat(message.getEnum()).isEqualTo(EnumProto3.DEFAULT);
        break;
      default:
        fail("Unexpected oneof set: " + message.getOneofCase());
    }
  }

  @Test
  public void itDoesntSetEnumWhenNullInJson() {
    String json = "{\"enum\":null}";
    HasOneof message = camelCase().readValue(json, HasOneof.class);
    switch (message.getOneofCase()) {
      case ONEOF_NOT_SET:
        assertThat(message).isEqualTo(HasOneof.getDefaultInstance());
        break;
      default:
        fail("Unexpected oneof set: " + message.getOneofCase());
    }
  }

  @Test
  public void itSetsProto2MessageWhenPresentInJson() {
    String json = "{\"proto2Message\":{\"string\":\"test\"}}";
    HasOneof message = camelCase().readValue(json, HasOneof.class);
    switch (message.getOneofCase()) {
      case PROTO2_MESSAGE:
        assertThat(message.getProto2Message())
          .isEqualTo(AllFields.newBuilder().setString("test").build());
        break;
      default:
        fail("Unexpected oneof set: " + message.getOneofCase());
    }
  }

  @Test
  public void itSetsProto2MessageWhenEmptyInJson() {
    String json = "{\"proto2Message\":{}}";
    HasOneof message = camelCase().readValue(json, HasOneof.class);
    switch (message.getOneofCase()) {
      case PROTO2_MESSAGE:
        assertThat(message.getProto2Message()).isEqualTo(AllFields.getDefaultInstance());
        break;
      default:
        fail("Unexpected oneof set: " + message.getOneofCase());
    }
  }

  @Test
  public void itDoesntSetProto2MessageWhenNullInJson() {
    String json = "{\"proto2Message\":null}";
    HasOneof message = camelCase().readValue(json, HasOneof.class);
    switch (message.getOneofCase()) {
      case ONEOF_NOT_SET:
        assertThat(message).isEqualTo(HasOneof.getDefaultInstance());
        break;
      default:
        fail("Unexpected oneof set: " + message.getOneofCase());
    }
  }

  @Test
  public void itSetsProto3MessageWhenPresentInJson() {
    String json = "{\"proto3Message\":{\"string\":\"test\"}}";
    HasOneof message = camelCase().readValue(json, HasOneof.class);
    switch (message.getOneofCase()) {
      case PROTO3_MESSAGE:
        assertThat(message.getProto3Message())
          .isEqualTo(AllFieldsProto3.newBuilder().setString("test").build());
        break;
      default:
        fail("Unexpected oneof set: " + message.getOneofCase());
    }
  }

  @Test
  public void itSetsProto3MessageWhenEmptyInJson() {
    String json = "{\"proto3Message\":{}}";
    HasOneof message = camelCase().readValue(json, HasOneof.class);
    switch (message.getOneofCase()) {
      case PROTO3_MESSAGE:
        assertThat(message.getProto3Message())
          .isEqualTo(AllFieldsProto3.getDefaultInstance());
        break;
      default:
        fail("Unexpected oneof set: " + message.getOneofCase());
    }
  }

  @Test
  public void itDoesntSetProto3MessageWhenNullInJson() {
    String json = "{\"proto3Message\":null}";
    HasOneof message = camelCase().readValue(json, HasOneof.class);
    switch (message.getOneofCase()) {
      case ONEOF_NOT_SET:
        assertThat(message).isEqualTo(HasOneof.getDefaultInstance());
        break;
      default:
        fail("Unexpected oneof set: " + message.getOneofCase());
    }
  }

  private ObjectNode nullProto2Message() {
    Map<String, JsonNode> fields = new LinkedHashMap<>();
    fields.put("double", NullNode.getInstance());
    fields.put("float", NullNode.getInstance());
    fields.put("int32", NullNode.getInstance());
    fields.put("int64", NullNode.getInstance());
    fields.put("uint32", NullNode.getInstance());
    fields.put("uint64", NullNode.getInstance());
    fields.put("sint32", NullNode.getInstance());
    fields.put("sint64", NullNode.getInstance());
    fields.put("fixed32", NullNode.getInstance());
    fields.put("fixed64", NullNode.getInstance());
    fields.put("sfixed32", NullNode.getInstance());
    fields.put("sfixed64", NullNode.getInstance());
    fields.put("bool", NullNode.getInstance());
    fields.put("string", NullNode.getInstance());
    fields.put("bytes", NullNode.getInstance());
    fields.put("enum", NullNode.getInstance());
    fields.put("nested", NullNode.getInstance());
    ObjectNode node = camelCase().createObjectNode();
    node.setAll(fields);
    return node;
  }

  private ObjectNode nullProto3Message() {
    ObjectNode node = camelCase().createObjectNode();
    node
      .put("double", 0.0d)
      .put("float", 0.0d)
      .put("int32", 0)
      .put("int64", 0)
      .put("uint32", 0)
      .put("uint64", 0)
      .put("sint32", 0)
      .put("sint64", 0)
      .put("fixed32", 0)
      .put("fixed64", 0)
      .put("sfixed32", 0)
      .put("sfixed64", 0)
      .put("bool", false)
      .put("string", "")
      .put("bytes", "")
      .put("enum", "DEFAULT")
      .set("nested", NullNode.getInstance());
    return node;
  }
}
