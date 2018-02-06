package com.hubspot.jackson.datatype.protobuf;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.hubspot.jackson.datatype.protobuf.util.ProtobufCreator;
import com.hubspot.jackson.datatype.protobuf.util.TestProtobuf.AllFields;
import com.hubspot.jackson.datatype.protobuf.util.TestProtobuf.Nested;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static com.hubspot.jackson.datatype.protobuf.util.ObjectMapperHelper.camelCase;
import static com.hubspot.jackson.datatype.protobuf.util.ObjectMapperHelper.underscore;
import static com.hubspot.jackson.datatype.protobuf.util.ObjectMapperHelper.writeAndReadBack;
import static org.assertj.core.api.Assertions.assertThat;

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
    List<AllFields.Builder> builders = ProtobufCreator.createBuilder(AllFields.Builder.class, 10);

    List<AllFields.Builder> parsed = writeAndReadBack(camelCase(), builders);

    assertThat(build(parsed)).isEqualTo(build(builders));
  }

  @Test
  public void testSingleMessageUnderscore() {
    AllFields message = ProtobufCreator.create(AllFields.class);

    AllFields parsed = writeAndReadBack(underscore(), message);

    assertThat(parsed).isEqualTo(message);
  }

  @Test
  public void testMultipleMessagesUnderscore() {
    List<AllFields> messages = ProtobufCreator.create(AllFields.class, 10);

    List<AllFields> parsed = writeAndReadBack(underscore(), messages);

    assertThat(parsed).isEqualTo(messages);
  }

  @Test
  public void testSingleBuilderUnderscore() {
    AllFields.Builder builder = ProtobufCreator.createBuilder(AllFields.Builder.class);

    AllFields.Builder parsed = writeAndReadBack(underscore(), builder);

    assertThat(parsed.build()).isEqualTo(builder.build());
  }

  @Test
  public void testMultipleBuildersUnderscore() {
    List<AllFields.Builder> builders = ProtobufCreator.createBuilder(AllFields.Builder.class, 10);

    List<AllFields.Builder> parsed = writeAndReadBack(underscore(), builders);

    assertThat(build(parsed)).isEqualTo(build(builders));
  }

  @Test
  public void testEmptyNestedObject() throws IOException {
    String json = "{\"nested\":{}}";

    AllFields parsed = camelCase().readValue(json, AllFields.class);

    assertThat(parsed.getNested()).isEqualTo(Nested.getDefaultInstance());
  }

  private static List<AllFields> build(List<AllFields.Builder> builders) {
    return Lists.transform(builders, new Function<AllFields.Builder, AllFields>() {

      @Override
      public AllFields apply(AllFields.Builder builder) {
        return builder.build();
      }
    });
  }
}
