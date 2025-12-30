package com.hubspot.jackson3.datatype.protobuf;

import static com.hubspot.jackson3.datatype.protobuf.util.ObjectMapperHelper.camelCase;
import static com.hubspot.jackson3.datatype.protobuf.util.ObjectMapperHelper.writeAndReadBack;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableList;
import com.hubspot.jackson3.datatype.protobuf.util.ObjectMapperHelper;
import com.hubspot.jackson3.datatype.protobuf.util.ProtobufCreator;
import com.hubspot.jackson3.datatype.protobuf.util.TestProtobuf.AllFields;
import com.hubspot.jackson3.datatype.protobuf.util.TestProtobuf.AllFields.Builder;
import com.hubspot.jackson3.datatype.protobuf.util.TestProtobuf.Nested;
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

    AllFields parsed = writeAndReadBack(ObjectMapperHelper.underscore(), message);

    assertThat(parsed).isEqualTo(message);
  }

  @Test
  public void testMultipleMessagesUnderscore() {
    List<AllFields> messages = ProtobufCreator.create(AllFields.class, 10);

    List<AllFields> parsed = writeAndReadBack(ObjectMapperHelper.underscore(), messages);

    assertThat(parsed).isEqualTo(messages);
  }

  @Test
  public void testSingleBuilderUnderscore() {
    AllFields.Builder builder = ProtobufCreator.createBuilder(AllFields.Builder.class);

    AllFields.Builder parsed = writeAndReadBack(ObjectMapperHelper.underscore(), builder);

    assertThat(parsed.build()).isEqualTo(builder.build());
  }

  @Test
  public void testMultipleBuildersUnderscore() {
    List<AllFields.Builder> builders = ProtobufCreator.createBuilder(
      AllFields.Builder.class,
      10
    );

    List<AllFields.Builder> parsed = writeAndReadBack(
      ObjectMapperHelper.underscore(),
      builders
    );

    assertThat(build(parsed)).isEqualTo(build(builders));
  }

  @Test
  public void testEmptyNestedObject() {
    String json = "{\"nested\":{}}";

    AllFields parsed = camelCase().readValue(json, AllFields.class);

    assertThat(parsed.getNested()).isEqualTo(Nested.getDefaultInstance());
  }

  private static List<AllFields> build(List<AllFields.Builder> builders) {
    return builders.stream().map(Builder::build).collect(ImmutableList.toImmutableList());
  }
}
