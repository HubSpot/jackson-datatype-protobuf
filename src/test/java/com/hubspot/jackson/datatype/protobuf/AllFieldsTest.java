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
import com.hubspot.jackson.datatype.protobuf.util.TestProtobuf.AllFields;
import com.hubspot.jackson.datatype.protobuf.util.TestProtobuf.AllFields.Builder;
import com.hubspot.jackson.datatype.protobuf.util.TestProtobuf.Nested;
import java.io.IOException;
import java.util.List;
import org.junit.Test;

public class AllFieldsTest {

  @Test
  public void testSingleMessageCamelCase() {
    AllFields message = ProtobufCreator.create(AllFields.class);

    AllFields parsed = writeAndReadBack(camelCase(), message);

    assertThat(parsed).isEqualTo(message);
  }

  @Test
  public void testMultipleMessagesCamelCase() {
    List<AllFields> messages = ProtobufCreator.create(AllFields.class, 10);

    List<AllFields> parsed = writeAndReadBack(camelCase(), messages);

    assertThat(parsed).isEqualTo(messages);
  }

  @Test
  public void testSingleBuilderCamelCase() {
    AllFields.Builder builder = ProtobufCreator.createBuilder(AllFields.Builder.class);

    AllFields.Builder parsed = writeAndReadBack(camelCase(), builder);

    assertThat(parsed.build()).isEqualTo(builder.build());
  }

  @Test
  public void testMultipleBuildersCamelCase() {
    List<AllFields.Builder> builders = ProtobufCreator.createBuilder(
      AllFields.Builder.class,
      10
    );

    List<AllFields.Builder> parsed = writeAndReadBack(camelCase(), builders);

    assertThat(build(parsed)).isEqualTo(build(builders));
  }

  @Test
  public void testSingleMessageUnderscore() {
    AllFields message = ProtobufCreator.create(AllFields.class);

    AllFields parsed = writeAndReadBack(ObjectMapperHelper.oldUnderscore(), message);

    assertThat(parsed).isEqualTo(message);
  }

  @Test
  public void testSingleMessageMixedUnderscoreNamingStrategies() throws IOException {
    AllFields message = ProtobufCreator.create(AllFields.class);

    JsonNode json = newUnderscore().valueToTree(message);
    AllFields parsed = oldUnderscore().treeToValue(json, AllFields.class);

    assertThat(parsed).isEqualTo(message);

    json = oldUnderscore().valueToTree(message);
    parsed = newUnderscore().treeToValue(json, AllFields.class);

    assertThat(parsed).isEqualTo(message);
  }

  @Test
  public void testMultipleMessagesUnderscore() {
    List<AllFields> messages = ProtobufCreator.create(AllFields.class, 10);

    List<AllFields> parsed = writeAndReadBack(
      ObjectMapperHelper.oldUnderscore(),
      messages
    );

    assertThat(parsed).isEqualTo(messages);
  }

  @Test
  public void testMultipleMessagesMixedUnderscoreNamingStrategies() {
    List<AllFields> messages = ProtobufCreator.create(AllFields.class, 10);

    JsonNode json = newUnderscore().valueToTree(messages);
    List<AllFields> parsed = parseList(oldUnderscore(), AllFields.class, json);

    assertThat(parsed).isEqualTo(messages);

    json = oldUnderscore().valueToTree(messages);
    parsed = parseList(newUnderscore(), AllFields.class, json);

    assertThat(parsed).isEqualTo(messages);
  }

  @Test
  public void testSingleBuilderUnderscore() {
    AllFields.Builder builder = ProtobufCreator.createBuilder(AllFields.Builder.class);

    AllFields.Builder parsed = writeAndReadBack(
      ObjectMapperHelper.oldUnderscore(),
      builder
    );

    assertThat(parsed.build()).isEqualTo(builder.build());
  }

  @Test
  public void testSingleBuilderMixedUnderscoreNamingStrategies() throws IOException {
    AllFields.Builder builder = ProtobufCreator.createBuilder(AllFields.Builder.class);

    JsonNode json = newUnderscore().valueToTree(builder);
    AllFields.Builder parsed = oldUnderscore().treeToValue(json, AllFields.Builder.class);

    assertThat(parsed.build()).isEqualTo(builder.build());

    json = oldUnderscore().valueToTree(builder);
    parsed = newUnderscore().treeToValue(json, AllFields.Builder.class);

    assertThat(parsed.build()).isEqualTo(builder.build());
  }

  @Test
  public void testMultipleBuildersUnderscore() {
    List<AllFields.Builder> builders = ProtobufCreator.createBuilder(
      AllFields.Builder.class,
      10
    );

    List<AllFields.Builder> parsed = writeAndReadBack(
      ObjectMapperHelper.oldUnderscore(),
      builders
    );

    assertThat(build(parsed)).isEqualTo(build(builders));
  }

  @Test
  public void testMultipleBuildersMixedUnderscoreNamingStrategies() {
    List<AllFields.Builder> builders = ProtobufCreator.createBuilder(
      AllFields.Builder.class,
      10
    );

    JsonNode json = newUnderscore().valueToTree(builders);
    List<AllFields.Builder> parsed = parseList(
      oldUnderscore(),
      AllFields.Builder.class,
      json
    );

    assertThat(build(parsed)).isEqualTo(build(builders));

    json = oldUnderscore().valueToTree(builders);
    parsed = parseList(newUnderscore(), AllFields.Builder.class, json);

    assertThat(build(parsed)).isEqualTo(build(builders));
  }

  @Test
  public void testEmptyNestedObject() throws IOException {
    String json = "{\"nested\":{}}";

    AllFields parsed = camelCase().readValue(json, AllFields.class);

    assertThat(parsed.getNested()).isEqualTo(Nested.getDefaultInstance());
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

  private static List<AllFields> build(List<AllFields.Builder> builders) {
    return builders.stream().map(Builder::build).collect(ImmutableList.toImmutableList());
  }
}
