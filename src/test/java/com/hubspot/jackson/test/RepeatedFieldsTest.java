package com.hubspot.jackson.test;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.hubspot.jackson.test.util.ProtobufCreator;
import com.hubspot.jackson.test.util.TestProtobuf.RepeatedFields;
import org.junit.Test;

import java.util.List;

import static com.hubspot.jackson.test.util.ObjectMapperHelper.camelCase;
import static com.hubspot.jackson.test.util.ObjectMapperHelper.underscore;
import static com.hubspot.jackson.test.util.ObjectMapperHelper.writeAndReadBack;
import static org.fest.assertions.api.Assertions.assertThat;

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
    RepeatedFields.Builder builder = ProtobufCreator.createBuilder(RepeatedFields.Builder.class);

    RepeatedFields.Builder parsed = writeAndReadBack(camelCase(), builder);

    assertThat(parsed.build()).isEqualTo(builder.build());
  }

  @Test
  public void testMultipleBuildersCamelCase() {
    List<RepeatedFields.Builder> builders = ProtobufCreator.createBuilder(RepeatedFields.Builder.class, 10);

    List<RepeatedFields.Builder> parsed = writeAndReadBack(camelCase(), builders);

    assertThat(build(parsed)).isEqualTo(build(builders));
  }

  @Test
  public void testSingleMessageUnderscore() {
    RepeatedFields message = ProtobufCreator.create(RepeatedFields.class);

    RepeatedFields parsed = writeAndReadBack(underscore(), message);

    assertThat(parsed).isEqualTo(message);
  }

  @Test
  public void testMultipleMessagesUnderscore() {
    List<RepeatedFields> messages = ProtobufCreator.create(RepeatedFields.class, 10);

    List<RepeatedFields> parsed = writeAndReadBack(underscore(), messages);

    assertThat(parsed).isEqualTo(messages);
  }

  @Test
  public void testSingleBuilderUnderscore() {
    RepeatedFields.Builder builder = ProtobufCreator.createBuilder(RepeatedFields.Builder.class);

    RepeatedFields.Builder parsed = writeAndReadBack(underscore(), builder);

    assertThat(parsed.build()).isEqualTo(builder.build());
  }

  @Test
  public void testMultipleBuildersUnderscore() {
    List<RepeatedFields.Builder> builders = ProtobufCreator.createBuilder(RepeatedFields.Builder.class, 10);

    List<RepeatedFields.Builder> parsed = writeAndReadBack(underscore(), builders);

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
