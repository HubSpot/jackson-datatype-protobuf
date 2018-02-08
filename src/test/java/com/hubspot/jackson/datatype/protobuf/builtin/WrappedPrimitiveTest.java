package com.hubspot.jackson.datatype.protobuf.builtin;

import static com.hubspot.jackson.datatype.protobuf.util.ObjectMapperHelper.camelCase;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.BoolValue;
import com.google.protobuf.ByteString;
import com.google.protobuf.BytesValue;
import com.google.protobuf.DoubleValue;
import com.google.protobuf.FloatValue;
import com.google.protobuf.Int32Value;
import com.google.protobuf.Int64Value;
import com.google.protobuf.StringValue;
import com.google.protobuf.UInt32Value;
import com.google.protobuf.UInt64Value;
import com.hubspot.jackson.datatype.protobuf.util.BuiltInProtobufs.HasWrappedPrimitives;

public class WrappedPrimitiveTest {
  private static final DoubleValue DOUBLE_WRAPPER = DoubleValue.newBuilder().setValue(1.0d).build();
  private static final FloatValue FLOAT_WRAPPER = FloatValue.newBuilder().setValue(2.0f).build();
  private static final Int64Value INT64_WRAPPER = Int64Value.newBuilder().setValue(3).build();
  private static final UInt64Value UINT64_WRAPPER = UInt64Value.newBuilder().setValue(4).build();
  private static final Int32Value INT32_WRAPPER = Int32Value.newBuilder().setValue(5).build();
  private static final UInt32Value UINT32_WRAPPER = UInt32Value.newBuilder().setValue(6).build();
  private static final BoolValue BOOL_WRAPPER = BoolValue.newBuilder().setValue(true).build();
  private static final StringValue STRING_WRAPPER = StringValue.newBuilder().setValue("test_string").build();
  private static final BytesValue BYTES_WRAPPER = BytesValue.newBuilder().setValue(ByteString.copyFromUtf8("test_bytes")).build();

  @Test
  public void itWritesFieldsWhenSetWithDefaultInclusion() throws IOException {
    HasWrappedPrimitives message = fullyPopulatedMessage();
    JsonNode json = toNode(message, camelCase());
    assertThat(json).isEqualTo(fullyPopulatedJsonNode(camelCase()));
  }

  @Test
  public void itWritesZeroWhenSetToDefaultInstanceWithDefaultInclusion() throws IOException {
    HasWrappedPrimitives message = defaultPopulatedMessage();
    JsonNode json = toNode(message, camelCase());
    assertThat(json).isEqualTo(defaultPopulatedJsonNode(camelCase()));
  }

  @Test
  public void itOmitsFieldsWhenNotSetWithDefaultInclusion() throws IOException {
    HasWrappedPrimitives message = emptyMessage();
    JsonNode json = toNode(message, camelCase());
    assertThat(json).isEqualTo(emptyJsonNode(camelCase()));
  }

  @Test
  public void itWritesFieldsWhenSetWithNonDefaultInclusion() throws IOException {
    HasWrappedPrimitives message = fullyPopulatedMessage();
    JsonNode json = toNode(message, camelCase(Include.NON_DEFAULT));
    assertThat(json).isEqualTo(fullyPopulatedJsonNode(camelCase(Include.NON_DEFAULT)));
  }

  @Test
  public void itWritesZeroWhenSetToDefaultInstanceWithNonDefaultInclusion() throws IOException {
    HasWrappedPrimitives message = defaultPopulatedMessage();
    JsonNode json = toNode(message, camelCase(Include.NON_DEFAULT));
    assertThat(json).isEqualTo(defaultPopulatedJsonNode(camelCase(Include.NON_DEFAULT)));
  }

  @Test
  public void itOmitsFieldsWhenNotSetWithNonDefaultInclusion() throws IOException {
    HasWrappedPrimitives message = emptyMessage();
    JsonNode json = toNode(message, camelCase(Include.NON_DEFAULT));
    assertThat(json).isEqualTo(emptyJsonNode(camelCase(Include.NON_DEFAULT)));
  }

  @Test
  public void itWritesFieldsSetWithAlwaysInclusion() throws IOException {
    HasWrappedPrimitives message = fullyPopulatedMessage();
    JsonNode json = toNode(message, camelCase(Include.ALWAYS));
    assertThat(json).isEqualTo(fullyPopulatedJsonNode(camelCase(Include.ALWAYS)));
  }

  @Test
  public void itWritesZeroWhenSetToDefaultInstanceWithAlwaysInclusion() throws IOException {
    HasWrappedPrimitives message = defaultPopulatedMessage();
    JsonNode json = toNode(message, camelCase(Include.ALWAYS));
    assertThat(json).isEqualTo(defaultPopulatedJsonNode(camelCase(Include.ALWAYS)));
  }

  @Test
  public void itWritesNullWhenNotSetWithAlwaysInclusion() throws IOException {
    HasWrappedPrimitives message = emptyMessage();
    JsonNode json = toNode(message, camelCase(Include.ALWAYS));
    assertThat(json).isEqualTo(nullPopulatedJsonNode(camelCase(Include.ALWAYS)));
  }

  @Test
  public void itWritesFieldsWhenSetWithNonNullInclusion() throws IOException {
    HasWrappedPrimitives message = fullyPopulatedMessage();
    JsonNode json = toNode(message, camelCase(Include.NON_NULL));
    assertThat(json).isEqualTo(fullyPopulatedJsonNode(camelCase(Include.NON_NULL)));
  }

  @Test
  public void itWritesZeroWhenSetToDefaultInstanceWithNonNullInclusion() throws IOException {
    HasWrappedPrimitives message = defaultPopulatedMessage();
    JsonNode json = toNode(message, camelCase(Include.NON_NULL));
    assertThat(json).isEqualTo(defaultPopulatedJsonNode(camelCase(Include.NON_NULL)));
  }

  @Test
  public void itOmitsFieldsWhenNotSetWithNonNullInclusion() throws IOException {
    HasWrappedPrimitives message = emptyMessage();
    JsonNode json = toNode(message, camelCase(Include.NON_NULL));
    assertThat(json).isEqualTo(emptyJsonNode(camelCase(Include.NON_NULL)));
  }

  @Test
  public void itSetsFieldsWhenPresentInJson() throws IOException {
    String json = camelCase().writeValueAsString(fullyPopulatedJsonNode(camelCase()));
    HasWrappedPrimitives message = camelCase().readValue(json, HasWrappedPrimitives.class);
    assertThat(message.hasDoubleWrapper()).isTrue();
    assertThat(message.getDoubleWrapper()).isEqualTo(DOUBLE_WRAPPER);
    assertThat(message.hasFloatWrapper()).isTrue();
    assertThat(message.getFloatWrapper()).isEqualTo(FLOAT_WRAPPER);
    assertThat(message.hasInt64Wrapper()).isTrue();
    assertThat(message.getInt64Wrapper()).isEqualTo(INT64_WRAPPER);
    assertThat(message.hasUint64Wrapper()).isTrue();
    assertThat(message.getUint64Wrapper()).isEqualTo(UINT64_WRAPPER);
    assertThat(message.hasInt32Wrapper()).isTrue();
    assertThat(message.getInt32Wrapper()).isEqualTo(INT32_WRAPPER);
    assertThat(message.hasUint32Wrapper()).isTrue();
    assertThat(message.getUint32Wrapper()).isEqualTo(UINT32_WRAPPER);
    assertThat(message.hasBoolWrapper()).isTrue();
    assertThat(message.getBoolWrapper()).isEqualTo(BOOL_WRAPPER);
    assertThat(message.hasStringWrapper()).isTrue();
    assertThat(message.getStringWrapper()).isEqualTo(STRING_WRAPPER);
    assertThat(message.hasBytesWrapper()).isTrue();
    assertThat(message.getBytesWrapper()).isEqualTo(BYTES_WRAPPER);
  }

  @Test
  public void itSetsFieldsWhenZeroInJson() throws IOException {
    String json = camelCase().writeValueAsString(defaultPopulatedJsonNode(camelCase()));
    HasWrappedPrimitives message = camelCase().readValue(json, HasWrappedPrimitives.class);
    assertThat(message.hasDoubleWrapper()).isTrue();
    assertThat(message.getDoubleWrapper()).isEqualTo(DoubleValue.getDefaultInstance());
    assertThat(message.hasFloatWrapper()).isTrue();
    assertThat(message.getFloatWrapper()).isEqualTo(FloatValue.getDefaultInstance());
    assertThat(message.hasInt64Wrapper()).isTrue();
    assertThat(message.getInt64Wrapper()).isEqualTo(Int64Value.getDefaultInstance());
    assertThat(message.hasUint64Wrapper()).isTrue();
    assertThat(message.getUint64Wrapper()).isEqualTo(UInt64Value.getDefaultInstance());
    assertThat(message.hasInt32Wrapper()).isTrue();
    assertThat(message.getInt32Wrapper()).isEqualTo(Int32Value.getDefaultInstance());
    assertThat(message.hasUint32Wrapper()).isTrue();
    assertThat(message.getUint32Wrapper()).isEqualTo(UInt32Value.getDefaultInstance());
    assertThat(message.hasBoolWrapper()).isTrue();
    assertThat(message.getBoolWrapper()).isEqualTo(BoolValue.getDefaultInstance());
    assertThat(message.hasStringWrapper()).isTrue();
    assertThat(message.getStringWrapper()).isEqualTo(StringValue.getDefaultInstance());
    assertThat(message.hasBytesWrapper()).isTrue();
    assertThat(message.getBytesWrapper()).isEqualTo(BytesValue.getDefaultInstance());
  }

  @Test
  public void itDoesntSetFieldsWhenNullInJson() throws IOException {
    String json = camelCase().writeValueAsString(nullPopulatedJsonNode(camelCase()));
    HasWrappedPrimitives message = camelCase().readValue(json, HasWrappedPrimitives.class);
    assertThat(message.hasDoubleWrapper()).isFalse();
    assertThat(message.hasFloatWrapper()).isFalse();
    assertThat(message.hasInt64Wrapper()).isFalse();
    assertThat(message.hasUint64Wrapper()).isFalse();
    assertThat(message.hasInt32Wrapper()).isFalse();
    assertThat(message.hasUint32Wrapper()).isFalse();
    assertThat(message.hasBoolWrapper()).isFalse();
    assertThat(message.hasStringWrapper()).isFalse();
    assertThat(message.hasBytesWrapper()).isFalse();
  }

  @Test
  public void itDoesntSetFieldsWhenMissingFromJson() throws IOException {
    String json = camelCase().writeValueAsString(emptyJsonNode(camelCase()));
    HasWrappedPrimitives message = camelCase().readValue(json, HasWrappedPrimitives.class);
    assertThat(message.hasDoubleWrapper()).isFalse();
    assertThat(message.hasFloatWrapper()).isFalse();
    assertThat(message.hasInt64Wrapper()).isFalse();
    assertThat(message.hasUint64Wrapper()).isFalse();
    assertThat(message.hasInt32Wrapper()).isFalse();
    assertThat(message.hasUint32Wrapper()).isFalse();
    assertThat(message.hasBoolWrapper()).isFalse();
    assertThat(message.hasStringWrapper()).isFalse();
    assertThat(message.hasBytesWrapper()).isFalse();
  }

  private static JsonNode toNode(HasWrappedPrimitives message, ObjectMapper mapper) throws IOException {
    String json = mapper.writeValueAsString(message);
    return mapper.readTree(json);
  }

  private static JsonNode fullyPopulatedJsonNode(ObjectMapper mapper) {
    return mapper.createObjectNode()
            .put("doubleWrapper", 1.0)
            .put("floatWrapper", 2.0)
            .put("int64Wrapper", 3)
            .put("uint64Wrapper", 4)
            .put("int32Wrapper", 5)
            .put("uint32Wrapper", 6)
            .put("boolWrapper", true)
            .put("stringWrapper", "test_string")
            .put("bytesWrapper", "dGVzdF9ieXRlcw==");
  }

  private static JsonNode defaultPopulatedJsonNode(ObjectMapper mapper) {
    return mapper.createObjectNode()
            .put("doubleWrapper", 0.0)
            .put("floatWrapper", 0.0)
            .put("int64Wrapper", 0)
            .put("uint64Wrapper", 0)
            .put("int32Wrapper", 0)
            .put("uint32Wrapper", 0)
            .put("boolWrapper", false)
            .put("stringWrapper", "")
            .put("bytesWrapper", "");
  }

  private static JsonNode nullPopulatedJsonNode(ObjectMapper mapper) {
    return mapper.createObjectNode()
            .putNull("doubleWrapper")
            .putNull("floatWrapper")
            .putNull("int64Wrapper")
            .putNull("uint64Wrapper")
            .putNull("int32Wrapper")
            .putNull("uint32Wrapper")
            .putNull("boolWrapper")
            .putNull("stringWrapper")
            .putNull("bytesWrapper");
  }

  private static JsonNode emptyJsonNode(ObjectMapper mapper) {
    return mapper.createObjectNode();
  }

  private static HasWrappedPrimitives fullyPopulatedMessage() {
    return HasWrappedPrimitives
            .newBuilder()
            .setDoubleWrapper(DOUBLE_WRAPPER)
            .setFloatWrapper(FLOAT_WRAPPER)
            .setInt64Wrapper(INT64_WRAPPER)
            .setUint64Wrapper(UINT64_WRAPPER)
            .setInt32Wrapper(INT32_WRAPPER)
            .setUint32Wrapper(UINT32_WRAPPER)
            .setBoolWrapper(BOOL_WRAPPER)
            .setStringWrapper(STRING_WRAPPER)
            .setBytesWrapper(BYTES_WRAPPER)
            .build();
  }

  private static HasWrappedPrimitives defaultPopulatedMessage() {
    return HasWrappedPrimitives
            .newBuilder()
            .setDoubleWrapper(DoubleValue.getDefaultInstance())
            .setFloatWrapper(FloatValue.getDefaultInstance())
            .setInt64Wrapper(Int64Value.getDefaultInstance())
            .setUint64Wrapper(UInt64Value.getDefaultInstance())
            .setInt32Wrapper(Int32Value.getDefaultInstance())
            .setUint32Wrapper(UInt32Value.getDefaultInstance())
            .setBoolWrapper(BoolValue.getDefaultInstance())
            .setStringWrapper(StringValue.getDefaultInstance())
            .setBytesWrapper(BytesValue.getDefaultInstance())
            .build();
  }

  private static HasWrappedPrimitives emptyMessage() {
    return HasWrappedPrimitives.newBuilder().build();
  }
}
