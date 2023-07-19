package com.hubspot.jackson.datatype.protobuf.proto3;

import static com.hubspot.jackson.datatype.protobuf.util.ObjectMapperHelper.camelCase;
import static com.hubspot.jackson.datatype.protobuf.util.ObjectMapperHelper.newUnderscore;
import static com.hubspot.jackson.datatype.protobuf.util.ObjectMapperHelper.oldUnderscore;
import static com.hubspot.jackson.datatype.protobuf.util.ObjectMapperHelper.writeAndReadBack;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.hubspot.jackson.datatype.protobuf.util.ObjectMapperHelper;
import com.hubspot.jackson.datatype.protobuf.util.ProtobufCreator;
import com.hubspot.jackson.datatype.protobuf.util.TestProtobuf3.RepeatedFieldsProto3;
import com.hubspot.jackson.datatype.protobuf.util.TestProtobuf3.RepeatedFieldsProto3.Builder;
import java.io.IOException;
import java.util.List;
import org.junit.Test;

public class RepeatedFieldsProto3Test {

  @Test
  public void testSingleMessageCamelCase() {
    RepeatedFieldsProto3 message = ProtobufCreator.create(RepeatedFieldsProto3.class);

    RepeatedFieldsProto3 parsed = writeAndReadBack(camelCase(), message);

    assertThat(parsed).isEqualTo(message);
  }

  @Test
  public void testMultipleMessagesCamelCase() {
    List<RepeatedFieldsProto3> messages = ProtobufCreator.create(
      RepeatedFieldsProto3.class,
      10
    );

    List<RepeatedFieldsProto3> parsed = writeAndReadBack(camelCase(), messages);

    assertThat(parsed).isEqualTo(messages);
  }

  @Test
  public void testSingleBuilderCamelCase() {
    RepeatedFieldsProto3.Builder builder = ProtobufCreator.createBuilder(
      RepeatedFieldsProto3.Builder.class
    );

    RepeatedFieldsProto3.Builder parsed = writeAndReadBack(camelCase(), builder);

    assertThat(parsed.build()).isEqualTo(builder.build());
  }

  @Test
  public void testMultipleBuildersCamelCase() {
    List<RepeatedFieldsProto3.Builder> builders = ProtobufCreator.createBuilder(
      RepeatedFieldsProto3.Builder.class,
      10
    );

    List<RepeatedFieldsProto3.Builder> parsed = writeAndReadBack(camelCase(), builders);

    assertThat(build(parsed)).isEqualTo(build(builders));
  }

  @Test
  public void testSingleMessageUnderscore() {
    RepeatedFieldsProto3 message = ProtobufCreator.create(RepeatedFieldsProto3.class);

    RepeatedFieldsProto3 parsed = writeAndReadBack(
      ObjectMapperHelper.oldUnderscore(),
      message
    );

    assertThat(parsed).isEqualTo(message);
  }

  @Test
  public void testSingleMessageMixedUnderscoreNamingStrategies() throws IOException {
    RepeatedFieldsProto3 message = ProtobufCreator.create(RepeatedFieldsProto3.class);

    JsonNode json = newUnderscore().valueToTree(message);
    RepeatedFieldsProto3 parsed = oldUnderscore()
      .treeToValue(json, RepeatedFieldsProto3.class);

    assertThat(parsed).isEqualTo(message);

    json = oldUnderscore().valueToTree(message);
    parsed = newUnderscore().treeToValue(json, RepeatedFieldsProto3.class);

    assertThat(parsed).isEqualTo(message);
  }

  @Test
  public void testMultipleMessagesUnderscore() {
    List<RepeatedFieldsProto3> messages = ProtobufCreator.create(
      RepeatedFieldsProto3.class,
      10
    );

    List<RepeatedFieldsProto3> parsed = writeAndReadBack(
      ObjectMapperHelper.oldUnderscore(),
      messages
    );

    assertThat(parsed).isEqualTo(messages);
  }

  @Test
  public void testMultipleMessagesMixedUnderscoreNamingStrategies() {
    List<RepeatedFieldsProto3> messages = ProtobufCreator.create(
      RepeatedFieldsProto3.class,
      10
    );

    JsonNode json = newUnderscore().valueToTree(messages);
    List<RepeatedFieldsProto3> parsed = parseList(
      oldUnderscore(),
      RepeatedFieldsProto3.class,
      json
    );

    assertThat(parsed).isEqualTo(messages);

    json = oldUnderscore().valueToTree(messages);
    parsed = parseList(newUnderscore(), RepeatedFieldsProto3.class, json);

    assertThat(parsed).isEqualTo(messages);
  }

  @Test
  public void testSingleBuilderUnderscore() {
    RepeatedFieldsProto3.Builder builder = ProtobufCreator.createBuilder(
      RepeatedFieldsProto3.Builder.class
    );

    RepeatedFieldsProto3.Builder parsed = writeAndReadBack(
      ObjectMapperHelper.oldUnderscore(),
      builder
    );

    assertThat(parsed.build()).isEqualTo(builder.build());
  }

  @Test
  public void testSingleBuilderMixedUnderscoreNamingStrategies() throws IOException {
    RepeatedFieldsProto3.Builder builder = ProtobufCreator.createBuilder(
      RepeatedFieldsProto3.Builder.class
    );

    JsonNode json = newUnderscore().valueToTree(builder);
    RepeatedFieldsProto3.Builder parsed = oldUnderscore()
      .treeToValue(json, RepeatedFieldsProto3.Builder.class);

    assertThat(parsed.build()).isEqualTo(builder.build());

    json = oldUnderscore().valueToTree(builder);
    parsed = newUnderscore().treeToValue(json, RepeatedFieldsProto3.Builder.class);

    assertThat(parsed.build()).isEqualTo(builder.build());
  }

  @Test
  public void testMultipleBuildersUnderscore() {
    List<RepeatedFieldsProto3.Builder> builders = ProtobufCreator.createBuilder(
      RepeatedFieldsProto3.Builder.class,
      10
    );

    List<RepeatedFieldsProto3.Builder> parsed = writeAndReadBack(
      ObjectMapperHelper.oldUnderscore(),
      builders
    );

    assertThat(build(parsed)).isEqualTo(build(builders));
  }

  @Test
  public void testMultipleBuildersMixedUnderscoreNamingStrategies() {
    List<RepeatedFieldsProto3.Builder> builders = ProtobufCreator.createBuilder(
      RepeatedFieldsProto3.Builder.class,
      10
    );

    JsonNode json = newUnderscore().valueToTree(builders);
    List<RepeatedFieldsProto3.Builder> parsed = parseList(
      oldUnderscore(),
      RepeatedFieldsProto3.Builder.class,
      json
    );

    assertThat(build(parsed)).isEqualTo(build(builders));

    json = oldUnderscore().valueToTree(builders);
    parsed = parseList(newUnderscore(), RepeatedFieldsProto3.Builder.class, json);

    assertThat(build(parsed)).isEqualTo(build(builders));
  }

  private static <T> List<T> parseList(
    ObjectMapper mapper,
    Class<T> type,
    JsonNode json
  ) {
    return mapper.convertValue(
      json,
      mapper.getTypeFactory().constructCollectionType(List.class, type)
    );
  }

  private static List<RepeatedFieldsProto3> build(
    List<RepeatedFieldsProto3.Builder> builders
  ) {
    return builders.stream().map(Builder::build).collect(ImmutableList.toImmutableList());
  }
}
