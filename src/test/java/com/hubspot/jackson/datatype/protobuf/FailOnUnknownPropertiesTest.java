package com.hubspot.jackson.datatype.protobuf;

import static com.hubspot.jackson.datatype.protobuf.util.ObjectMapperHelper.camelCase;
import static com.hubspot.jackson.datatype.protobuf.util.ObjectMapperHelper.create;
import static com.hubspot.jackson.datatype.protobuf.util.ObjectMapperHelper.toTree;
import static org.assertj.core.api.Assertions.assertThat;

import com.hubspot.jackson.datatype.protobuf.util.ProtobufCreator;
import com.hubspot.jackson.datatype.protobuf.util.TestProtobuf.AllFields;
import org.junit.Test;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.DatabindException;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

public class FailOnUnknownPropertiesTest {

  @Test(expected = DatabindException.class)
  public void testEnabled() throws JacksonException {
    ObjectMapper mapper = objectMapper(true);

    mapper.treeToValue(buildNode(), AllFields.class);
  }

  @Test
  public void testDisabled() throws JacksonException {
    ObjectMapper mapper = objectMapper(false);

    mapper.treeToValue(buildNode(), AllFields.class);
  }

  @Test
  public void testAdvancesParser() throws JacksonException {
    AllFields message = ProtobufCreator.create(AllFields.class);

    ObjectNode tree = (ObjectNode) toTree(camelCase(), message);
    addObject(tree);
    addArray(tree);

    AllFields parsed = objectMapper(false).treeToValue(tree, AllFields.class);

    assertThat(parsed).isEqualTo(message);
  }

  private static JsonNode buildNode() {
    ObjectNode node = camelCase().createObjectNode();

    addObject(node);
    addArray(node);

    return node;
  }

  private static void addObject(ObjectNode node) {
    node.putObject("fakeObject").put("fakeProperty", true);
  }

  private static void addArray(ObjectNode node) {
    node.putArray("fakeArray").add(true);
  }

  private static ObjectMapper objectMapper(boolean enabled) {
    if (enabled) {
      return create()
        .rebuild()
        .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .build();
    } else {
      return create()
        .rebuild()
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .build();
    }
  }
}
