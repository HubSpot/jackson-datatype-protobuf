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
import com.hubspot.jackson.datatype.protobuf.util.TestProtobuf.RepeatedFields;
import com.hubspot.jackson.datatype.protobuf.util.TestProtobuf.RepeatedFields.Builder;

public class RepeatedExtensionsTest {
  private static final ExtensionRegistry EXTENSION_REGISTRY = TestExtensionRegistry.getInstance();

  @Test
  public void testSingleMessageCamelCase() {
    RepeatedFields message = ProtobufCreator.create(RepeatedFields.class, EXTENSION_REGISTRY);

    RepeatedFields parsed = writeAndReadBack(camelCase(EXTENSION_REGISTRY), message);

    assertThat(parsed).isEqualTo(message);
  }

  @Test
  public void testMultipleMessagesCamelCase() {
    List<RepeatedFields> messages = ProtobufCreator.create(RepeatedFields.class, EXTENSION_REGISTRY, 10);

    List<RepeatedFields> parsed = writeAndReadBack(camelCase(EXTENSION_REGISTRY), messages);

    assertThat(parsed).isEqualTo(messages);
  }

  @Test
  public void testSingleBuilderCamelCase() {
    RepeatedFields.Builder builder = ProtobufCreator.createBuilder(RepeatedFields.Builder.class, EXTENSION_REGISTRY);

    RepeatedFields.Builder parsed = writeAndReadBack(camelCase(EXTENSION_REGISTRY), builder);

    assertThat(parsed.build()).isEqualTo(builder.build());
  }

  @Test
  public void testMultipleBuildersCamelCase() {
    List<RepeatedFields.Builder> builders = ProtobufCreator.createBuilder(RepeatedFields.Builder.class, EXTENSION_REGISTRY, 10);

    List<RepeatedFields.Builder> parsed = writeAndReadBack(camelCase(EXTENSION_REGISTRY), builders);

    assertThat(build(parsed)).isEqualTo(build(builders));
  }

  @Test
  public void testSingleMessageUnderscore() {
    RepeatedFields message = ProtobufCreator.create(RepeatedFields.class, EXTENSION_REGISTRY);

    RepeatedFields parsed = writeAndReadBack(oldUnderscore(EXTENSION_REGISTRY), message);

    assertThat(parsed).isEqualTo(message);
  }

  @Test
  public void testSingleMessageMixedUnderscoreNamingStrategies() throws IOException {
    RepeatedFields message = ProtobufCreator.create(RepeatedFields.class, EXTENSION_REGISTRY);

    JsonNode json = newUnderscore(EXTENSION_REGISTRY).valueToTree(message);
    RepeatedFields parsed = oldUnderscore(EXTENSION_REGISTRY).treeToValue(json, RepeatedFields.class);

    assertThat(parsed).isEqualTo(message);

    json = oldUnderscore(EXTENSION_REGISTRY).valueToTree(message);
    parsed = newUnderscore(EXTENSION_REGISTRY).treeToValue(json, RepeatedFields.class);

    assertThat(parsed).isEqualTo(message);
  }

  @Test
  public void testMultipleMessagesUnderscore() {
    List<RepeatedFields> messages = ProtobufCreator.create(RepeatedFields.class, EXTENSION_REGISTRY, 10);

    List<RepeatedFields> parsed = writeAndReadBack(oldUnderscore(EXTENSION_REGISTRY), messages);

    assertThat(parsed).isEqualTo(messages);
  }

  @Test
  public void testMultipleMessagesMixedUnderscoreNamingStrategies() {
    List<RepeatedFields> messages = ProtobufCreator.create(RepeatedFields.class, EXTENSION_REGISTRY, 10);

    JsonNode json = newUnderscore(EXTENSION_REGISTRY).valueToTree(messages);
    List<RepeatedFields> parsed = parseList(oldUnderscore(EXTENSION_REGISTRY), RepeatedFields.class, json);

    assertThat(parsed).isEqualTo(messages);

    json = oldUnderscore(EXTENSION_REGISTRY).valueToTree(messages);
    parsed = parseList(newUnderscore(EXTENSION_REGISTRY), RepeatedFields.class, json);

    assertThat(parsed).isEqualTo(messages);
  }

  @Test
  public void testSingleBuilderUnderscore() {
    RepeatedFields.Builder builder = ProtobufCreator.createBuilder(RepeatedFields.Builder.class, EXTENSION_REGISTRY);

    RepeatedFields.Builder parsed = writeAndReadBack(oldUnderscore(EXTENSION_REGISTRY), builder);

    assertThat(parsed.build()).isEqualTo(builder.build());
  }

  @Test
  public void testSingleBuilderMixedUnderscoreNamingStrategies() throws IOException {
    RepeatedFields.Builder builder = ProtobufCreator.createBuilder(RepeatedFields.Builder.class, EXTENSION_REGISTRY);

    JsonNode json = newUnderscore(EXTENSION_REGISTRY).valueToTree(builder);
    RepeatedFields.Builder parsed = oldUnderscore(EXTENSION_REGISTRY).treeToValue(json, RepeatedFields.Builder.class);

    assertThat(parsed.build()).isEqualTo(builder.build());

    json = oldUnderscore(EXTENSION_REGISTRY).valueToTree(builder);
    parsed = newUnderscore(EXTENSION_REGISTRY).treeToValue(json, RepeatedFields.Builder.class);

    assertThat(parsed.build()).isEqualTo(builder.build());
  }

  @Test
  public void testMultipleBuildersUnderscore() {
    List<RepeatedFields.Builder> builders = ProtobufCreator.createBuilder(RepeatedFields.Builder.class, EXTENSION_REGISTRY, 10);

    List<RepeatedFields.Builder> parsed = writeAndReadBack(oldUnderscore(EXTENSION_REGISTRY), builders);

    assertThat(build(parsed)).isEqualTo(build(builders));
  }

  @Test
  public void testMultipleBuildersMixedUnderscoreNamingStrategies() {
    List<RepeatedFields.Builder> builders = ProtobufCreator.createBuilder(RepeatedFields.Builder.class, EXTENSION_REGISTRY, 10);

    JsonNode json = newUnderscore(EXTENSION_REGISTRY).valueToTree(builders);
    List<RepeatedFields.Builder> parsed = parseList(oldUnderscore(EXTENSION_REGISTRY), RepeatedFields.Builder.class, json);

    assertThat(build(parsed)).isEqualTo(build(builders));

    json = oldUnderscore(EXTENSION_REGISTRY).valueToTree(builders);
    parsed = parseList(newUnderscore(EXTENSION_REGISTRY), RepeatedFields.Builder.class, json);

    assertThat(build(parsed)).isEqualTo(build(builders));
  }

  private static <T> List<T> parseList(ObjectMapper mapper, Class<T> type, JsonNode json) {
    try {
       return mapper.treeToValue(json, mapper.getTypeFactory().constructCollectionType(List.class, type));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static List<RepeatedFields> build(List<Builder> builders) {
    return builders.stream().map(Builder::build).collect(ImmutableList.toImmutableList());
  }
}
