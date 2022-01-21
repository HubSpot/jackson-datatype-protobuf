package com.hubspot.jackson.datatype.protobuf.proto3;

import static com.hubspot.jackson.datatype.protobuf.util.ObjectMapperHelper.camelCase;
import static com.hubspot.jackson.datatype.protobuf.util.ObjectMapperHelper.newUnderscore;
import static com.hubspot.jackson.datatype.protobuf.util.ObjectMapperHelper.oldUnderscore;
import static com.hubspot.jackson.datatype.protobuf.util.ObjectMapperHelper.writeAndReadBack;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.hubspot.jackson.datatype.protobuf.util.ObjectMapperHelper;
import com.hubspot.jackson.datatype.protobuf.util.ProtobufCreator;
import com.hubspot.jackson.datatype.protobuf.util.TestProtobuf3.AllFieldsProto3;
import com.hubspot.jackson.datatype.protobuf.util.TestProtobuf3.AllFieldsProto3.Builder;
import com.hubspot.jackson.datatype.protobuf.util.TestProtobuf3.NestedProto3;

public class AllFieldsProto3Test {

  @Test
  public void testSingleMessageCamelCase() {
    AllFieldsProto3 message = ProtobufCreator.create(AllFieldsProto3.class);

    AllFieldsProto3 parsed = writeAndReadBack(camelCase(), message);

    assertThat(parsed).isEqualTo(message);
  }

  @Test
  public void testMultipleMessagesCamelCase() {
    List<AllFieldsProto3> messages = ProtobufCreator.create(AllFieldsProto3.class, 10);

    List<AllFieldsProto3> parsed = writeAndReadBack(camelCase(), messages);

    assertThat(parsed).isEqualTo(messages);
  }

  @Test
  public void testSingleBuilderCamelCase() {
    AllFieldsProto3.Builder builder = ProtobufCreator.createBuilder(AllFieldsProto3.Builder.class);

    AllFieldsProto3.Builder parsed = writeAndReadBack(camelCase(), builder);

    assertThat(parsed.build()).isEqualTo(builder.build());
  }

  @Test
  public void testMultipleBuildersCamelCase() {
    List<AllFieldsProto3.Builder> builders = ProtobufCreator.createBuilder(AllFieldsProto3.Builder.class, 10);

    List<AllFieldsProto3.Builder> parsed = writeAndReadBack(camelCase(), builders);

    assertThat(build(parsed)).isEqualTo(build(builders));
  }

  @Test
  public void testSingleMessageUnderscore() {
    AllFieldsProto3 message = ProtobufCreator.create(AllFieldsProto3.class);

    AllFieldsProto3 parsed = writeAndReadBack(ObjectMapperHelper.oldUnderscore(), message);

    assertThat(parsed).isEqualTo(message);
  }

  @Test
  public void testSingleMessageMixedUnderscoreNamingStrategies() throws IOException {
    AllFieldsProto3 message = ProtobufCreator.create(AllFieldsProto3.class);

    JsonNode json = newUnderscore().valueToTree(message);
    AllFieldsProto3 parsed = oldUnderscore().treeToValue(json, AllFieldsProto3.class);

    assertThat(parsed).isEqualTo(message);

    json = oldUnderscore().valueToTree(message);
    parsed = newUnderscore().treeToValue(json, AllFieldsProto3.class);

    assertThat(parsed).isEqualTo(message);
  }

  @Test
  public void testMultipleMessagesUnderscore() {
    List<AllFieldsProto3> messages = ProtobufCreator.create(AllFieldsProto3.class, 10);

    List<AllFieldsProto3> parsed = writeAndReadBack(ObjectMapperHelper.oldUnderscore(), messages);

    assertThat(parsed).isEqualTo(messages);
  }

  @Test
  public void testMultipleMessagesMixedUnderscoreNamingStrategies() {
    List<AllFieldsProto3> messages = ProtobufCreator.create(AllFieldsProto3.class, 10);

    JsonNode json = newUnderscore().valueToTree(messages);
    List<AllFieldsProto3> parsed = parseList(oldUnderscore(), AllFieldsProto3.class, json);

    assertThat(parsed).isEqualTo(messages);

    json = oldUnderscore().valueToTree(messages);
    parsed = parseList(newUnderscore(), AllFieldsProto3.class, json);

    assertThat(parsed).isEqualTo(messages);
  }

  @Test
  public void testSingleBuilderUnderscore() {
    AllFieldsProto3.Builder builder = ProtobufCreator.createBuilder(AllFieldsProto3.Builder.class);

    AllFieldsProto3.Builder parsed = writeAndReadBack(ObjectMapperHelper.oldUnderscore(), builder);

    assertThat(parsed.build()).isEqualTo(builder.build());
  }

  @Test
  public void testSingleBuilderMixedUnderscoreNamingStrategies() throws IOException {
    AllFieldsProto3.Builder builder = ProtobufCreator.createBuilder(AllFieldsProto3.Builder.class);

    JsonNode json = newUnderscore().valueToTree(builder);
    AllFieldsProto3.Builder parsed = oldUnderscore().treeToValue(json, AllFieldsProto3.Builder.class);

    assertThat(parsed.build()).isEqualTo(builder.build());

    json = oldUnderscore().valueToTree(builder);
    parsed = newUnderscore().treeToValue(json, AllFieldsProto3.Builder.class);

    assertThat(parsed.build()).isEqualTo(builder.build());
  }

  @Test
  public void testMultipleBuildersUnderscore() {
    List<AllFieldsProto3.Builder> builders = ProtobufCreator.createBuilder(AllFieldsProto3.Builder.class, 10);

    List<AllFieldsProto3.Builder> parsed = writeAndReadBack(ObjectMapperHelper.oldUnderscore(), builders);

    assertThat(build(parsed)).isEqualTo(build(builders));
  }

  @Test
  public void testMultipleBuildersMixedUnderscoreNamingStrategies() {
    List<AllFieldsProto3.Builder> builders = ProtobufCreator.createBuilder(AllFieldsProto3.Builder.class, 10);

    JsonNode json = newUnderscore().valueToTree(builders);
    List<AllFieldsProto3.Builder> parsed = parseList(oldUnderscore(), AllFieldsProto3.Builder.class, json);

    assertThat(build(parsed)).isEqualTo(build(builders));

    json = oldUnderscore().valueToTree(builders);
    parsed = parseList(newUnderscore(), AllFieldsProto3.Builder.class, json);

    assertThat(build(parsed)).isEqualTo(build(builders));
  }

  @Test
  public void testEmptyNestedObject() throws IOException {
    String json = "{\"nested\":{}}";

    AllFieldsProto3 parsed = camelCase().readValue(json, AllFieldsProto3.class);

    assertThat(parsed.getNested()).isEqualTo(NestedProto3.getDefaultInstance());
  }

  private static <T> List<T> parseList(ObjectMapper mapper, Class<T> type, JsonNode json) {
    try {
      return mapper.treeToValue(json, mapper.getTypeFactory().constructCollectionType(List.class, type));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static List<AllFieldsProto3> build(List<AllFieldsProto3.Builder> builders) {
    return builders.stream().map(Builder::build).collect(ImmutableList.toImmutableList());
  }
}
