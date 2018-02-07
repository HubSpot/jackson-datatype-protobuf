package com.hubspot.jackson.datatype.protobuf.proto3;

import static com.hubspot.jackson.datatype.protobuf.util.ObjectMapperHelper.camelCase;
import static com.hubspot.jackson.datatype.protobuf.util.ObjectMapperHelper.underscore;
import static com.hubspot.jackson.datatype.protobuf.util.ObjectMapperHelper.writeAndReadBack;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.hubspot.jackson.datatype.protobuf.util.ProtobufCreator;
import com.hubspot.jackson.datatype.protobuf.util.TestProtobuf3.RepeatedFieldsProto3;

public class RepeatedFieldsProto3Test {

  @Test
  public void testSingleMessageCamelCase() {
    RepeatedFieldsProto3 message = ProtobufCreator.create(RepeatedFieldsProto3.class);

    RepeatedFieldsProto3 parsed = writeAndReadBack(camelCase(), message);

    assertThat(parsed).isEqualTo(message);
  }

  @Test
  public void testMultipleMessagesCamelCase() {
    List<RepeatedFieldsProto3> messages = ProtobufCreator.create(RepeatedFieldsProto3.class, 10);

    List<RepeatedFieldsProto3> parsed = writeAndReadBack(camelCase(), messages);

    assertThat(parsed).isEqualTo(messages);
  }

  @Test
  public void testSingleBuilderCamelCase() {
    RepeatedFieldsProto3.Builder builder = ProtobufCreator.createBuilder(RepeatedFieldsProto3.Builder.class);

    RepeatedFieldsProto3.Builder parsed = writeAndReadBack(camelCase(), builder);

    assertThat(parsed.build()).isEqualTo(builder.build());
  }

  @Test
  public void testMultipleBuildersCamelCase() {
    List<RepeatedFieldsProto3.Builder> builders = ProtobufCreator.createBuilder(RepeatedFieldsProto3.Builder.class, 10);

    List<RepeatedFieldsProto3.Builder> parsed = writeAndReadBack(camelCase(), builders);

    assertThat(build(parsed)).isEqualTo(build(builders));
  }

  @Test
  public void testSingleMessageUnderscore() {
    RepeatedFieldsProto3 message = ProtobufCreator.create(RepeatedFieldsProto3.class);

    RepeatedFieldsProto3 parsed = writeAndReadBack(underscore(), message);

    assertThat(parsed).isEqualTo(message);
  }

  @Test
  public void testMultipleMessagesUnderscore() {
    List<RepeatedFieldsProto3> messages = ProtobufCreator.create(RepeatedFieldsProto3.class, 10);

    List<RepeatedFieldsProto3> parsed = writeAndReadBack(underscore(), messages);

    assertThat(parsed).isEqualTo(messages);
  }

  @Test
  public void testSingleBuilderUnderscore() {
    RepeatedFieldsProto3.Builder builder = ProtobufCreator.createBuilder(RepeatedFieldsProto3.Builder.class);

    RepeatedFieldsProto3.Builder parsed = writeAndReadBack(underscore(), builder);

    assertThat(parsed.build()).isEqualTo(builder.build());
  }

  @Test
  public void testMultipleBuildersUnderscore() {
    List<RepeatedFieldsProto3.Builder> builders = ProtobufCreator.createBuilder(RepeatedFieldsProto3.Builder.class, 10);

    List<RepeatedFieldsProto3.Builder> parsed = writeAndReadBack(underscore(), builders);

    assertThat(build(parsed)).isEqualTo(build(builders));
  }

  private static List<RepeatedFieldsProto3> build(List<RepeatedFieldsProto3.Builder> builders) {
    return Lists.transform(builders, new Function<RepeatedFieldsProto3.Builder, RepeatedFieldsProto3>() {

      @Override
      public RepeatedFieldsProto3 apply(RepeatedFieldsProto3.Builder builder) {
        return builder.build();
      }
    });
  }
}
