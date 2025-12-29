package com.hubspot.jackson.datatype.protobuf;

import static com.hubspot.jackson.datatype.protobuf.util.ObjectMapperHelper.camelCase;
import static com.hubspot.jackson.datatype.protobuf.util.ObjectMapperHelper.underscore;
import static com.hubspot.jackson.datatype.protobuf.util.ObjectMapperHelper.writeAndReadBack;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableList;
import com.google.protobuf.ExtensionRegistry;
import com.hubspot.jackson.datatype.protobuf.util.ProtobufCreator;
import com.hubspot.jackson.datatype.protobuf.util.TestExtensionRegistry;
import com.hubspot.jackson.datatype.protobuf.util.TestProtobuf.AllFields;
import com.hubspot.jackson.datatype.protobuf.util.TestProtobuf.AllFields.Builder;
import com.hubspot.jackson.datatype.protobuf.util.TestProtobuf.Nested;
import java.util.List;
import org.junit.Test;

public class AllExtensionsTest {

  private static final ExtensionRegistry EXTENSION_REGISTRY =
    TestExtensionRegistry.getInstance();

  @Test
  public void testSingleMessageCamelCase() {
    AllFields message = ProtobufCreator.create(AllFields.class, EXTENSION_REGISTRY);

    AllFields parsed = writeAndReadBack(camelCase(EXTENSION_REGISTRY), message);

    assertThat(parsed).isEqualTo(message);
  }

  @Test
  public void testMultipleMessagesCamelCase() {
    List<AllFields> messages = ProtobufCreator.create(
      AllFields.class,
      EXTENSION_REGISTRY,
      10
    );

    List<AllFields> parsed = writeAndReadBack(camelCase(EXTENSION_REGISTRY), messages);

    assertThat(parsed).isEqualTo(messages);
  }

  @Test
  public void testSingleBuilderCamelCase() {
    AllFields.Builder builder = ProtobufCreator.createBuilder(
      AllFields.Builder.class,
      EXTENSION_REGISTRY
    );

    AllFields.Builder parsed = writeAndReadBack(camelCase(EXTENSION_REGISTRY), builder);

    assertThat(parsed.build()).isEqualTo(builder.build());
  }

  @Test
  public void testMultipleBuildersCamelCase() {
    List<AllFields.Builder> builders = ProtobufCreator.createBuilder(
      AllFields.Builder.class,
      EXTENSION_REGISTRY,
      10
    );

    List<AllFields.Builder> parsed = writeAndReadBack(
      camelCase(EXTENSION_REGISTRY),
      builders
    );

    assertThat(build(parsed)).isEqualTo(build(builders));
  }

  @Test
  public void testSingleMessageUnderscore() {
    AllFields message = ProtobufCreator.create(AllFields.class, EXTENSION_REGISTRY);

    AllFields parsed = writeAndReadBack(underscore(EXTENSION_REGISTRY), message);

    assertThat(parsed).isEqualTo(message);
  }

  @Test
  public void testMultipleMessagesUnderscore() {
    List<AllFields> messages = ProtobufCreator.create(
      AllFields.class,
      EXTENSION_REGISTRY,
      10
    );

    List<AllFields> parsed = writeAndReadBack(underscore(EXTENSION_REGISTRY), messages);

    assertThat(parsed).isEqualTo(messages);
  }

  @Test
  public void testSingleBuilderUnderscore() {
    AllFields.Builder builder = ProtobufCreator.createBuilder(
      AllFields.Builder.class,
      EXTENSION_REGISTRY
    );

    AllFields.Builder parsed = writeAndReadBack(underscore(EXTENSION_REGISTRY), builder);

    assertThat(parsed.build()).isEqualTo(builder.build());
  }

  @Test
  public void testMultipleBuildersUnderscore() {
    List<AllFields.Builder> builders = ProtobufCreator.createBuilder(
      AllFields.Builder.class,
      EXTENSION_REGISTRY,
      10
    );

    List<AllFields.Builder> parsed = writeAndReadBack(
      underscore(EXTENSION_REGISTRY),
      builders
    );

    assertThat(build(parsed)).isEqualTo(build(builders));
  }

  @Test
  public void testEmptyNestedObject() {
    String json = "{\"nested\":{}}";

    AllFields parsed = camelCase(EXTENSION_REGISTRY).readValue(json, AllFields.class);

    assertThat(parsed.getNested()).isEqualTo(Nested.getDefaultInstance());
  }

  private static List<AllFields> build(List<AllFields.Builder> builders) {
    return builders.stream().map(Builder::build).collect(ImmutableList.toImmutableList());
  }
}
