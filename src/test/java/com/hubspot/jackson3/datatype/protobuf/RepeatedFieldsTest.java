package com.hubspot.jackson3.datatype.protobuf;

import static com.hubspot.jackson3.datatype.protobuf.util.ObjectMapperHelper.camelCase;
import static com.hubspot.jackson3.datatype.protobuf.util.ObjectMapperHelper.writeAndReadBack;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableList;
import com.hubspot.jackson3.datatype.protobuf.util.ObjectMapperHelper;
import com.hubspot.jackson3.datatype.protobuf.util.ProtobufCreator;
import com.hubspot.jackson3.datatype.protobuf.util.TestProtobuf.RepeatedFields;
import com.hubspot.jackson3.datatype.protobuf.util.TestProtobuf.RepeatedFields.Builder;
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

    RepeatedFields parsed = writeAndReadBack(ObjectMapperHelper.underscore(), message);

    assertThat(parsed).isEqualTo(message);
  }

  @Test
  public void testMultipleMessagesUnderscore() {
    List<RepeatedFields> messages = ProtobufCreator.create(RepeatedFields.class, 10);

    List<RepeatedFields> parsed = writeAndReadBack(
      ObjectMapperHelper.underscore(),
      messages
    );

    assertThat(parsed).isEqualTo(messages);
  }

  @Test
  public void testSingleBuilderUnderscore() {
    RepeatedFields.Builder builder = ProtobufCreator.createBuilder(
      RepeatedFields.Builder.class
    );

    RepeatedFields.Builder parsed = writeAndReadBack(
      ObjectMapperHelper.underscore(),
      builder
    );

    assertThat(parsed.build()).isEqualTo(builder.build());
  }

  @Test
  public void testMultipleBuildersUnderscore() {
    List<RepeatedFields.Builder> builders = ProtobufCreator.createBuilder(
      RepeatedFields.Builder.class,
      10
    );

    List<RepeatedFields.Builder> parsed = writeAndReadBack(
      ObjectMapperHelper.underscore(),
      builders
    );

    assertThat(build(parsed)).isEqualTo(build(builders));
  }

  private static List<RepeatedFields> build(List<RepeatedFields.Builder> builders) {
    return builders.stream().map(Builder::build).collect(ImmutableList.toImmutableList());
  }
}
