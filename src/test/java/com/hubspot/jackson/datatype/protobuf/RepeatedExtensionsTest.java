package com.hubspot.jackson.datatype.protobuf;

import static com.hubspot.jackson.datatype.protobuf.util.ObjectMapperHelper.camelCase;
import static com.hubspot.jackson.datatype.protobuf.util.ObjectMapperHelper.underscore;
import static com.hubspot.jackson.datatype.protobuf.util.ObjectMapperHelper.writeAndReadBack;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.protobuf.ExtensionRegistry;
import com.hubspot.jackson.datatype.protobuf.util.ProtobufCreator;
import com.hubspot.jackson.datatype.protobuf.util.TestExtensionRegistry;
import com.hubspot.jackson.datatype.protobuf.util.TestProtobuf.RepeatedFields;

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

    RepeatedFields parsed = writeAndReadBack(underscore(EXTENSION_REGISTRY), message);

    assertThat(parsed).isEqualTo(message);
  }

  @Test
  public void testMultipleMessagesUnderscore() {
    List<RepeatedFields> messages = ProtobufCreator.create(RepeatedFields.class, EXTENSION_REGISTRY, 10);

    List<RepeatedFields> parsed = writeAndReadBack(underscore(EXTENSION_REGISTRY), messages);

    assertThat(parsed).isEqualTo(messages);
  }

  @Test
  public void testSingleBuilderUnderscore() {
    RepeatedFields.Builder builder = ProtobufCreator.createBuilder(RepeatedFields.Builder.class, EXTENSION_REGISTRY);

    RepeatedFields.Builder parsed = writeAndReadBack(underscore(EXTENSION_REGISTRY), builder);

    assertThat(parsed.build()).isEqualTo(builder.build());
  }

  @Test
  public void testMultipleBuildersUnderscore() {
    List<RepeatedFields.Builder> builders = ProtobufCreator.createBuilder(RepeatedFields.Builder.class, EXTENSION_REGISTRY, 10);

    List<RepeatedFields.Builder> parsed = writeAndReadBack(underscore(EXTENSION_REGISTRY), builders);

    assertThat(build(parsed)).isEqualTo(build(builders));
  }

  private static List<RepeatedFields> build(List<RepeatedFields.Builder> builders) {
    return Lists.transform(builders, new Function<RepeatedFields.Builder, RepeatedFields>() {

      @Override
      public RepeatedFields apply(RepeatedFields.Builder builder) {
        return builder.build();
      }
    });
  }
}
