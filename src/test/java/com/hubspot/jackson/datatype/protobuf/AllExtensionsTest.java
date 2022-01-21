package com.hubspot.jackson.datatype.protobuf;

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
import com.google.protobuf.ExtensionRegistry;
import com.hubspot.jackson.datatype.protobuf.util.ProtobufCreator;
import com.hubspot.jackson.datatype.protobuf.util.TestExtensionRegistry;
import com.hubspot.jackson.datatype.protobuf.util.TestProtobuf.AllFields;
import com.hubspot.jackson.datatype.protobuf.util.TestProtobuf.AllFields.Builder;
import com.hubspot.jackson.datatype.protobuf.util.TestProtobuf.Nested;

public class AllExtensionsTest {
  private static final ExtensionRegistry EXTENSION_REGISTRY = TestExtensionRegistry.getInstance();

  @Test
  public void testSingleMessageCamelCase() {
    AllFields message = ProtobufCreator.create(AllFields.class, EXTENSION_REGISTRY);

    AllFields parsed = writeAndReadBack(camelCase(EXTENSION_REGISTRY), message);

    assertThat(parsed).isEqualTo(message);
  }

  @Test
  public void testMultipleMessagesCamelCase() {
    List<AllFields> messages = ProtobufCreator.create(AllFields.class, EXTENSION_REGISTRY, 10);

    List<AllFields> parsed = writeAndReadBack(camelCase(EXTENSION_REGISTRY), messages);

    assertThat(parsed).isEqualTo(messages);
  }

  @Test
  public void testSingleBuilderCamelCase() {
    AllFields.Builder builder = ProtobufCreator.createBuilder(AllFields.Builder.class, EXTENSION_REGISTRY);

    AllFields.Builder parsed = writeAndReadBack(camelCase(EXTENSION_REGISTRY), builder);

    assertThat(parsed.build()).isEqualTo(builder.build());
  }

  @Test
  public void testMultipleBuildersCamelCase() {
    List<AllFields.Builder> builders = ProtobufCreator.createBuilder(AllFields.Builder.class, EXTENSION_REGISTRY, 10);

    List<AllFields.Builder> parsed = writeAndReadBack(camelCase(EXTENSION_REGISTRY), builders);

    assertThat(build(parsed)).isEqualTo(build(builders));
  }

  @Test
  public void testSingleMessageUnderscore() {
    AllFields message = ProtobufCreator.create(AllFields.class, EXTENSION_REGISTRY);

    AllFields parsed = writeAndReadBack(oldUnderscore(EXTENSION_REGISTRY), message);

    assertThat(parsed).isEqualTo(message);
  }

  @Test
  public void testSingleMessageMixedUnderscoreNamingStrategies() throws IOException {
    AllFields message = ProtobufCreator.create(AllFields.class, EXTENSION_REGISTRY);

    JsonNode json = newUnderscore(EXTENSION_REGISTRY).valueToTree(message);
    AllFields parsed = oldUnderscore(EXTENSION_REGISTRY).treeToValue(json, AllFields.class);

    assertThat(parsed).isEqualTo(message);

    json = oldUnderscore(EXTENSION_REGISTRY).valueToTree(message);
    parsed = newUnderscore(EXTENSION_REGISTRY).treeToValue(json, AllFields.class);

    assertThat(parsed).isEqualTo(message);
  }

  @Test
  public void testMultipleMessagesUnderscore() {
    List<AllFields> messages = ProtobufCreator.create(AllFields.class, EXTENSION_REGISTRY, 10);

    List<AllFields> parsed = writeAndReadBack(oldUnderscore(EXTENSION_REGISTRY), messages);

    assertThat(parsed).isEqualTo(messages);
  }

  @Test
  public void testMultipleMessagesMixedUnderscoreNamingStrategies() {
    List<AllFields> messages = ProtobufCreator.create(AllFields.class, EXTENSION_REGISTRY, 10);

    JsonNode json = newUnderscore(EXTENSION_REGISTRY).valueToTree(messages);
    List<AllFields> parsed = parseList(oldUnderscore(EXTENSION_REGISTRY), AllFields.class, json);

    assertThat(parsed).isEqualTo(messages);

    json = oldUnderscore(EXTENSION_REGISTRY).valueToTree(messages);
    parsed = parseList(newUnderscore(EXTENSION_REGISTRY), AllFields.class, json);

    assertThat(parsed).isEqualTo(messages);
  }

  @Test
  public void testSingleBuilderUnderscore() {
    AllFields.Builder builder = ProtobufCreator.createBuilder(AllFields.Builder.class, EXTENSION_REGISTRY);

    AllFields.Builder parsed = writeAndReadBack(oldUnderscore(EXTENSION_REGISTRY), builder);

    assertThat(parsed.build()).isEqualTo(builder.build());
  }

  @Test
  public void testSingleBuilderMixedUnderscoreNamingStrategies() throws IOException {
    AllFields.Builder builder = ProtobufCreator.createBuilder(AllFields.Builder.class, EXTENSION_REGISTRY);

    JsonNode json = newUnderscore(EXTENSION_REGISTRY).valueToTree(builder);
    AllFields.Builder parsed = oldUnderscore(EXTENSION_REGISTRY).treeToValue(json, AllFields.Builder.class);

    assertThat(parsed.build()).isEqualTo(builder.build());

    json = oldUnderscore(EXTENSION_REGISTRY).valueToTree(builder);
    parsed = newUnderscore(EXTENSION_REGISTRY).treeToValue(json, AllFields.Builder.class);

    assertThat(parsed.build()).isEqualTo(builder.build());
  }

  @Test
  public void testMultipleBuildersUnderscore() {
    List<AllFields.Builder> builders = ProtobufCreator.createBuilder(AllFields.Builder.class, EXTENSION_REGISTRY, 10);

    List<AllFields.Builder> parsed = writeAndReadBack(oldUnderscore(EXTENSION_REGISTRY), builders);

    assertThat(build(parsed)).isEqualTo(build(builders));
  }

  @Test
  public void testMultipleBuildersMixedUnderscoreNamingStrategies() {
    List<AllFields.Builder> builders = ProtobufCreator.createBuilder(AllFields.Builder.class, EXTENSION_REGISTRY, 10);

    JsonNode json = newUnderscore(EXTENSION_REGISTRY).valueToTree(builders);
    List<AllFields.Builder> parsed = parseList(oldUnderscore(EXTENSION_REGISTRY), AllFields.Builder.class, json);

    assertThat(build(parsed)).isEqualTo(build(builders));

    json = oldUnderscore(EXTENSION_REGISTRY).valueToTree(builders);
    parsed = parseList(newUnderscore(EXTENSION_REGISTRY), AllFields.Builder.class, json);

    assertThat(build(parsed)).isEqualTo(build(builders));
  }

  @Test
  public void testEmptyNestedObject() throws IOException {
    String json = "{\"nested\":{}}";

    AllFields parsed = camelCase(EXTENSION_REGISTRY).readValue(json, AllFields.class);

    assertThat(parsed.getNested()).isEqualTo(Nested.getDefaultInstance());
  }

  private static <T> List<T> parseList(ObjectMapper mapper, Class<T> type, JsonNode json) {
    return mapper.convertValue(json, mapper.getTypeFactory().constructCollectionType(List.class, type));
  }

  private static List<AllFields> build(List<AllFields.Builder> builders) {
    return builders.stream().map(Builder::build).collect(ImmutableList.toImmutableList());
  }
}
