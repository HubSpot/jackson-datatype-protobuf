package com.hubspot.jackson.datatype.protobuf;

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
import com.hubspot.jackson.datatype.protobuf.util.TestProtobuf.RepeatedFields;
import com.hubspot.jackson.datatype.protobuf.util.TestProtobuf.RepeatedFields.Builder;
import java.io.IOException;
import java.util.List;
import org.junit.Test;

public class RepeatedFieldsTest {

  @Test
  public void testSingleMessageCamelCase() {
    RepeatedFields message = ProtobufCreator.create(RepeatedFields.class);

    RepeatedFields parsed = writeAndReadBack(camelCase(), message);

    assertThat(parsed).isEqualTo(message);
  }

  @Test
  public void testMultipleMessagesCamelCase() {
    List<RepeatedFields> messages = ProtobufCreator.create(RepeatedFields.class, 10);

    List<RepeatedFields> parsed = writeAndReadBack(camelCase(), messages);

    assertThat(parsed).isEqualTo(messages);
  }

  @Test
  public void testSingleBuilderCamelCase() {
    RepeatedFields.Builder builder = ProtobufCreator.createBuilder(
      RepeatedFields.Builder.class
    );

    RepeatedFields.Builder parsed = writeAndReadBack(camelCase(), builder);

    assertThat(parsed.build()).isEqualTo(builder.build());
  }

  @Test
  public void testMultipleBuildersCamelCase() {
    List<RepeatedFields.Builder> builders = ProtobufCreator.createBuilder(
      RepeatedFields.Builder.class,
      10
    );

    List<RepeatedFields.Builder> parsed = writeAndReadBack(camelCase(), builders);

    assertThat(build(parsed)).isEqualTo(build(builders));
  }

  @Test
  public void testSingleMessageUnderscore() {
    RepeatedFields message = ProtobufCreator.create(RepeatedFields.class);

    RepeatedFields parsed = writeAndReadBack(ObjectMapperHelper.oldUnderscore(), message);

    assertThat(parsed).isEqualTo(message);
  }

  @Test
  public void testSingleMessageMixedUnderscoreNamingStrategies() throws IOException {
    RepeatedFields message = ProtobufCreator.create(RepeatedFields.class);

    JsonNode json = newUnderscore().valueToTree(message);
    RepeatedFields parsed = oldUnderscore().treeToValue(json, RepeatedFields.class);

    assertThat(parsed).isEqualTo(message);

    json = oldUnderscore().valueToTree(message);
    parsed = newUnderscore().treeToValue(json, RepeatedFields.class);

    assertThat(parsed).isEqualTo(message);
  }

  @Test
  public void testMultipleMessagesUnderscore() {
    List<RepeatedFields> messages = ProtobufCreator.create(RepeatedFields.class, 10);

    List<RepeatedFields> parsed = writeAndReadBack(
      ObjectMapperHelper.oldUnderscore(),
      messages
    );

    assertThat(parsed).isEqualTo(messages);
  }

  @Test
  public void testMultipleMessagesMixedUnderscoreNamingStrategies() {
    List<RepeatedFields> messages = ProtobufCreator.create(RepeatedFields.class, 10);

    JsonNode json = newUnderscore().valueToTree(messages);
    List<RepeatedFields> parsed = parseList(oldUnderscore(), RepeatedFields.class, json);

    assertThat(parsed).isEqualTo(messages);

    json = oldUnderscore().valueToTree(messages);
    parsed = parseList(newUnderscore(), RepeatedFields.class, json);

    assertThat(parsed).isEqualTo(messages);
  }

  @Test
  public void testSingleBuilderUnderscore() {
    RepeatedFields.Builder builder = ProtobufCreator.createBuilder(
      RepeatedFields.Builder.class
    );

    RepeatedFields.Builder parsed = writeAndReadBack(
      ObjectMapperHelper.oldUnderscore(),
      builder
    );

    assertThat(parsed.build()).isEqualTo(builder.build());
  }

  @Test
  public void testSingleBuilderMixedUnderscoreNamingStrategies() throws IOException {
    RepeatedFields.Builder builder = ProtobufCreator.createBuilder(
      RepeatedFields.Builder.class
    );

    JsonNode json = newUnderscore().valueToTree(builder);
    RepeatedFields.Builder parsed = oldUnderscore()
      .treeToValue(json, RepeatedFields.Builder.class);

    assertThat(parsed.build()).isEqualTo(builder.build());

    json = oldUnderscore().valueToTree(builder);
    parsed = newUnderscore().treeToValue(json, RepeatedFields.Builder.class);

    assertThat(parsed.build()).isEqualTo(builder.build());
  }

  @Test
  public void testMultipleBuildersUnderscore() {
    List<RepeatedFields.Builder> builders = ProtobufCreator.createBuilder(
      RepeatedFields.Builder.class,
      10
    );

    List<RepeatedFields.Builder> parsed = writeAndReadBack(
      ObjectMapperHelper.oldUnderscore(),
      builders
    );

    assertThat(build(parsed)).isEqualTo(build(builders));
  }

  @Test
  public void testMultipleBuildersMixedUnderscoreNamingStrategies() {
    List<RepeatedFields.Builder> builders = ProtobufCreator.createBuilder(
      RepeatedFields.Builder.class,
      10
    );

    JsonNode json = newUnderscore().valueToTree(builders);
    List<RepeatedFields.Builder> parsed = parseList(
      oldUnderscore(),
      RepeatedFields.Builder.class,
      json
    );

    assertThat(build(parsed)).isEqualTo(build(builders));

    json = oldUnderscore().valueToTree(builders);
    parsed = parseList(newUnderscore(), RepeatedFields.Builder.class, json);

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

  private static List<RepeatedFields> build(List<RepeatedFields.Builder> builders) {
    return builders.stream().map(Builder::build).collect(ImmutableList.toImmutableList());
  }
}
