package com.hubspot.jackson.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hubspot.jackson.test.util.ProtobufCreator;
import com.hubspot.jackson.test.util.TestProtobuf.AllFields;
import org.junit.Test;

import static com.hubspot.jackson.test.util.ObjectMapperHelper.camelCase;
import static com.hubspot.jackson.test.util.ObjectMapperHelper.toTree;
import static org.assertj.core.api.Assertions.assertThat;

public class FailOnUnknownPropertiesTest {

  @Test(expected = JsonMappingException.class)
  public void testEnabled() throws JsonProcessingException {
    ObjectMapper mapper = objectMapper(true);

    mapper.treeToValue(buildNode(), AllFields.class);
  }

  @Test
  public void testDisabled() throws JsonProcessingException {
    ObjectMapper mapper = objectMapper(false);

    mapper.treeToValue(buildNode(), AllFields.class);
  }

  @Test
  public void testAdvancesParser() throws JsonProcessingException {
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
      return camelCase().enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    } else {
      return camelCase().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }
  }
}
