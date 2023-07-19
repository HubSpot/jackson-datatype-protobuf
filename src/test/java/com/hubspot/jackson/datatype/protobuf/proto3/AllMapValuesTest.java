package com.hubspot.jackson.datatype.protobuf.proto3;

import static com.hubspot.jackson.datatype.protobuf.util.ObjectMapperHelper.camelCase;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.google.protobuf.Any;
import com.google.protobuf.BoolValue;
import com.google.protobuf.ByteString;
import com.google.protobuf.BytesValue;
import com.google.protobuf.DoubleValue;
import com.google.protobuf.Duration;
import com.google.protobuf.FieldMask;
import com.google.protobuf.FloatValue;
import com.google.protobuf.Int32Value;
import com.google.protobuf.Int64Value;
import com.google.protobuf.ListValue;
import com.google.protobuf.NullValue;
import com.google.protobuf.StringValue;
import com.google.protobuf.Struct;
import com.google.protobuf.Timestamp;
import com.google.protobuf.UInt32Value;
import com.google.protobuf.UInt64Value;
import com.google.protobuf.Value;
import com.hubspot.jackson.datatype.protobuf.util.BuiltInProtobufs.HasAllMapValues;
import com.hubspot.jackson.datatype.protobuf.util.TestProtobuf.AllFields;
import com.hubspot.jackson.datatype.protobuf.util.TestProtobuf3.AllFieldsProto3;
import com.hubspot.jackson.datatype.protobuf.util.TestProtobuf3.EnumProto3;
import java.io.IOException;
import org.junit.Test;

public class AllMapValuesTest {

  @Test
  public void itWritesAllMapValuesWhenPopulated() throws IOException {
    HasAllMapValues message = hasAllMapValues();
    String json = camelCase(Include.NON_DEFAULT).writeValueAsString(message);
    JsonNode node = camelCase().readTree(json);
    assertThat(node).isEqualTo(hasAllMapValuesNode());
  }

  @Test
  public void itWritesEmptyMapsWhenNotPopulated() throws IOException {
    HasAllMapValues message = HasAllMapValues.newBuilder().build();
    String json = camelCase().writeValueAsString(message);
    JsonNode node = camelCase().readTree(json);
    assertThat(node).isEqualTo(hasEmptyMapsNode());
  }

  @Test
  public void itReadsAllMapValuesWhenPopulated() throws IOException {
    String json = camelCase().writeValueAsString(hasAllMapValuesNode());
    HasAllMapValues message = camelCase().readValue(json, HasAllMapValues.class);
    assertThat(message).isEqualTo(hasAllMapValues());
  }

  @Test
  public void itDoesntSetMapFieldsWhenEmpty() throws IOException {
    String json = camelCase().writeValueAsString(hasEmptyMapsNode());
    HasAllMapValues message = camelCase().readValue(json, HasAllMapValues.class);
    assertThat(message).isEqualTo(HasAllMapValues.getDefaultInstance());
  }

  @Test
  public void itDoesntSetMapFieldsWhenNull() throws IOException {
    String json = camelCase().writeValueAsString(hasNullMapsNode());
    HasAllMapValues message = camelCase().readValue(json, HasAllMapValues.class);
    assertThat(message).isEqualTo(HasAllMapValues.getDefaultInstance());
  }

  private static HasAllMapValues hasAllMapValues() {
    Value value = Value.newBuilder().setStringValue("test").build();
    ByteString byteString = ByteString.copyFromUtf8("test");
    Any any = Any
      .newBuilder()
      .setTypeUrl("type.googleapis.com/google.protobuf.Value")
      .setValue(value.toByteString())
      .build();
    return HasAllMapValues
      .newBuilder()
      .putDoubleMap("double", 1.5d)
      .putFloatMap("float", 2.5f)
      .putInt32Map("int32", 1)
      .putInt64Map("int64", 2)
      .putUint32Map("uint32", 3)
      .putUint64Map("uint64", 4)
      .putSint32Map("sint32", 5)
      .putSint64Map("sint64", 6)
      .putFixed32Map("fixed32", 7)
      .putFixed64Map("fixed64", 8)
      .putSfixed32Map("sfixed32", 9)
      .putSfixed64Map("sfixed64", 10)
      .putBoolMap("bool", true)
      .putStringMap("string", "test")
      .putBytesMap("bytes", byteString)
      .putAnyMap("any", any)
      .putDurationMap("duration", Duration.newBuilder().setSeconds(30).build())
      .putFieldMaskMap(
        "field_mask",
        FieldMask.newBuilder().addPaths("path_one").addPaths("path_two").build()
      )
      .putListValueMap("list_value", ListValue.newBuilder().addValues(value).build())
      .putNullValueMap("null_value", NullValue.NULL_VALUE)
      .putStructMap("struct", Struct.newBuilder().putFields("field", value).build())
      .putTimestampMap("timestamp", Timestamp.newBuilder().setSeconds(946684800).build())
      .putValueMap("value", value)
      .putDoubleWrapperMap(
        "double_wrapper",
        DoubleValue.newBuilder().setValue(3.5d).build()
      )
      .putFloatWrapperMap("float_wrapper", FloatValue.newBuilder().setValue(4.5f).build())
      .putInt32WrapperMap("int32_wrapper", Int32Value.newBuilder().setValue(11).build())
      .putInt64WrapperMap("int64_wrapper", Int64Value.newBuilder().setValue(12).build())
      .putUint32WrapperMap(
        "uint32_wrapper",
        UInt32Value.newBuilder().setValue(13).build()
      )
      .putUint64WrapperMap(
        "uint64_wrapper",
        UInt64Value.newBuilder().setValue(14).build()
      )
      .putBoolWrapperMap("bool_wrapper", BoolValue.newBuilder().setValue(true).build())
      .putStringWrapperMap(
        "string_wrapper",
        StringValue.newBuilder().setValue("test").build()
      )
      .putBytesWrapperMap(
        "bytes_wrapper",
        BytesValue.newBuilder().setValue(byteString).build()
      )
      .putEnumMap("enum", EnumProto3.FIRST)
      .putProto2MessageMap("proto2", AllFields.newBuilder().setString("proto2").build())
      .putProto3MessageMap(
        "proto3",
        AllFieldsProto3.newBuilder().setString("proto3").build()
      )
      .build();
  }

  private static ObjectNode hasAllMapValuesNode() {
    ObjectNode node = newObjectNode();
    node.set("doubleMap", newObjectNode().put("double", 1.5d));
    node.set("floatMap", newObjectNode().put("float", 2.5d));
    node.set("int32Map", newObjectNode().put("int32", 1));
    node.set("int64Map", newObjectNode().put("int64", 2));
    node.set("uint32Map", newObjectNode().put("uint32", 3));
    node.set("uint64Map", newObjectNode().put("uint64", 4));
    node.set("sint32Map", newObjectNode().put("sint32", 5));
    node.set("sint64Map", newObjectNode().put("sint64", 6));
    node.set("fixed32Map", newObjectNode().put("fixed32", 7));
    node.set("fixed64Map", newObjectNode().put("fixed64", 8));
    node.set("sfixed32Map", newObjectNode().put("sfixed32", 9));
    node.set("sfixed64Map", newObjectNode().put("sfixed64", 10));
    node.set("boolMap", newObjectNode().put("bool", true));
    node.set("stringMap", newObjectNode().put("string", "test"));
    node.set("bytesMap", newObjectNode().put("bytes", "dGVzdA=="));
    node.set("anyMap", newObjectNode().set("any", anyNode()));
    node.set("durationMap", newObjectNode().put("duration", "30s"));
    node.set("fieldMaskMap", newObjectNode().put("field_mask", "pathOne,pathTwo"));
    node.set(
      "listValueMap",
      newObjectNode()
        .set("list_value", camelCase().createArrayNode().add(TextNode.valueOf("test")))
    );
    node.set("nullValueMap", newObjectNode().set("null_value", NullNode.getInstance()));
    node.set(
      "structMap",
      newObjectNode().set("struct", newObjectNode().put("field", "test"))
    );
    node.set("timestampMap", newObjectNode().put("timestamp", "2000-01-01T00:00:00Z"));
    node.set("valueMap", newObjectNode().put("value", "test"));
    node.set("doubleWrapperMap", newObjectNode().put("double_wrapper", 3.5d));
    node.set("floatWrapperMap", newObjectNode().put("float_wrapper", 4.5d));
    node.set("int32WrapperMap", newObjectNode().put("int32_wrapper", 11));
    node.set("int64WrapperMap", newObjectNode().put("int64_wrapper", 12));
    node.set("uint32WrapperMap", newObjectNode().put("uint32_wrapper", 13));
    node.set("uint64WrapperMap", newObjectNode().put("uint64_wrapper", 14));
    node.set("boolWrapperMap", newObjectNode().put("bool_wrapper", true));
    node.set("stringWrapperMap", newObjectNode().put("string_wrapper", "test"));
    node.set("bytesWrapperMap", newObjectNode().put("bytes_wrapper", "dGVzdA=="));
    node.set("enumMap", newObjectNode().put("enum", "FIRST"));
    node.set(
      "proto2MessageMap",
      newObjectNode().set("proto2", newObjectNode().put("string", "proto2"))
    );
    node.set(
      "proto3MessageMap",
      newObjectNode().set("proto3", newObjectNode().put("string", "proto3"))
    );
    return node;
  }

  private static ObjectNode anyNode() {
    byte[] bytes = Value.newBuilder().setStringValue("test").build().toByteArray();
    String base64 = camelCase().getSerializationConfig().getBase64Variant().encode(bytes);
    return newObjectNode()
      .put("typeUrl", "type.googleapis.com/google.protobuf.Value")
      .put("value", base64);
  }

  private static ObjectNode hasEmptyMapsNode() {
    ObjectNode node = newObjectNode();
    node.set("doubleMap", newObjectNode());
    node.set("floatMap", newObjectNode());
    node.set("int32Map", newObjectNode());
    node.set("int64Map", newObjectNode());
    node.set("uint32Map", newObjectNode());
    node.set("uint64Map", newObjectNode());
    node.set("sint32Map", newObjectNode());
    node.set("sint64Map", newObjectNode());
    node.set("fixed32Map", newObjectNode());
    node.set("fixed64Map", newObjectNode());
    node.set("sfixed32Map", newObjectNode());
    node.set("sfixed64Map", newObjectNode());
    node.set("boolMap", newObjectNode());
    node.set("stringMap", newObjectNode());
    node.set("bytesMap", newObjectNode());
    node.set("anyMap", newObjectNode());
    node.set("durationMap", newObjectNode());
    node.set("fieldMaskMap", newObjectNode());
    node.set("listValueMap", newObjectNode());
    node.set("nullValueMap", newObjectNode());
    node.set("structMap", newObjectNode());
    node.set("timestampMap", newObjectNode());
    node.set("valueMap", newObjectNode());
    node.set("doubleWrapperMap", newObjectNode());
    node.set("floatWrapperMap", newObjectNode());
    node.set("int32WrapperMap", newObjectNode());
    node.set("int64WrapperMap", newObjectNode());
    node.set("uint32WrapperMap", newObjectNode());
    node.set("uint64WrapperMap", newObjectNode());
    node.set("boolWrapperMap", newObjectNode());
    node.set("stringWrapperMap", newObjectNode());
    node.set("bytesWrapperMap", newObjectNode());
    node.set("enumMap", newObjectNode());
    node.set("proto2MessageMap", newObjectNode());
    node.set("proto3MessageMap", newObjectNode());
    return node;
  }

  private static ObjectNode hasNullMapsNode() {
    ObjectNode node = newObjectNode();
    node.set("doubleMap", NullNode.getInstance());
    node.set("floatMap", NullNode.getInstance());
    node.set("int32Map", NullNode.getInstance());
    node.set("int64Map", NullNode.getInstance());
    node.set("uint32Map", NullNode.getInstance());
    node.set("uint64Map", NullNode.getInstance());
    node.set("sint32Map", NullNode.getInstance());
    node.set("sint64Map", NullNode.getInstance());
    node.set("fixed32Map", NullNode.getInstance());
    node.set("fixed64Map", NullNode.getInstance());
    node.set("sfixed32Map", NullNode.getInstance());
    node.set("sfixed64Map", NullNode.getInstance());
    node.set("boolMap", NullNode.getInstance());
    node.set("stringMap", NullNode.getInstance());
    node.set("bytesMap", NullNode.getInstance());
    node.set("anyMap", NullNode.getInstance());
    node.set("durationMap", NullNode.getInstance());
    node.set("fieldMaskMap", NullNode.getInstance());
    node.set("listValueMap", NullNode.getInstance());
    node.set("nullValueMap", NullNode.getInstance());
    node.set("structMap", NullNode.getInstance());
    node.set("timestampMap", NullNode.getInstance());
    node.set("valueMap", NullNode.getInstance());
    node.set("doubleWrapperMap", NullNode.getInstance());
    node.set("floatWrapperMap", NullNode.getInstance());
    node.set("int32WrapperMap", NullNode.getInstance());
    node.set("int64WrapperMap", NullNode.getInstance());
    node.set("uint32WrapperMap", NullNode.getInstance());
    node.set("uint64WrapperMap", NullNode.getInstance());
    node.set("boolWrapperMap", NullNode.getInstance());
    node.set("stringWrapperMap", NullNode.getInstance());
    node.set("bytesWrapperMap", NullNode.getInstance());
    node.set("enumMap", NullNode.getInstance());
    node.set("proto2MessageMap", NullNode.getInstance());
    node.set("proto3MessageMap", NullNode.getInstance());
    return node;
  }

  private static ObjectNode newObjectNode() {
    return camelCase().createObjectNode();
  }
}
