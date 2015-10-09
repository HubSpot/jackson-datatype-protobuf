package com.hubspot.jackson.test;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.hubspot.jackson.test.util.TestProtobuf.AllFields;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import static com.hubspot.jackson.test.util.ObjectMapperHelper.camelCase;
import static org.assertj.core.api.Assertions.assertThat;

public class JsonInclusionTest {
  private static Set<String> allFields;
  private static Set<String> arrayFields;

  @BeforeClass
  public static void setup() {
    allFields = new HashSet<>();
    arrayFields = new HashSet<>();

    for (FieldDescriptor field : AllFields.getDescriptor().getFields()) {
      allFields.add(field.getName());
      if (field.isRepeated()) {
        arrayFields.add(field.getName());
      }
    }
  }

  @Test
  public void itWritesMissingFieldsAsNullWhenSerializationIncludeIsAlways() {
    AllFields message = AllFields.getDefaultInstance();

    JsonNode node = mapper(Include.ALWAYS).valueToTree(message);

    for (String field : allFields) {
      assertThat(node.has(field)).isTrue();
      if (arrayFields.contains(field)) {
        assertThat(node.get(field).isArray());
      } else {
        assertThat(node.get(field).isNull());
      }
    }
  }

  @Test
  public void itOnlyWritesArrayFieldsWhenSerializationIncludeIsNotAlways() {
    AllFields message = AllFields.getDefaultInstance();

    for (Include inclusion : EnumSet.complementOf(EnumSet.of(Include.ALWAYS))) {
      JsonNode node = mapper(inclusion).valueToTree(message);

      for (String field : allFields) {
        if (arrayFields.contains(field)) {
          assertThat(node.has(field)).isTrue();
          assertThat(node.get(field).isArray());
        } else {
          assertThat(node.has(field)).isFalse();
        }
      }
    }
  }

  private static ObjectMapper mapper(Include inclusion) {
    return camelCase().copy().setSerializationInclusion(inclusion);
  }
}
