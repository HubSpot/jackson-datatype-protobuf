package com.hubspot.jackson.test;

import static com.hubspot.jackson.test.util.ObjectMapperHelper.camelCase;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.CaseFormat;
import com.google.common.base.Enums;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.ExtensionRegistry.ExtensionInfo;
import com.hubspot.jackson.datatype.protobuf.ExtensionRegistryWrapper;
import com.hubspot.jackson.test.util.TestExtensionRegistry;
import com.hubspot.jackson.test.util.TestProtobuf.AllFields;

public class JsonInclusionTest {
  private static final EnumSet<Include> EXCLUDED_VALUES = presentValues("ALWAYS", "USE_DEFAULTS", "CUSTOM");
  private static final ExtensionRegistry EXTENSION_REGISTRY = TestExtensionRegistry.getInstance();

  private static Set<String> allFields;
  private static Set<String> allExtensionFields;
  private static Set<String> arrayFields;
  private static Set<String> arrayExtensionFields;

  @BeforeClass
  public static void setup() {
    allFields = new HashSet<>();
    arrayFields = new HashSet<>();

    Descriptor descriptor = AllFields.getDescriptor();
    for (FieldDescriptor field : descriptor.getFields()) {
      allFields.add(translate(field.getName()));
      if (field.isRepeated()) {
        arrayFields.add(translate(field.getName()));
      }
    }

    allExtensionFields = new HashSet<>();
    arrayExtensionFields = new HashSet<>();

    ExtensionRegistryWrapper extensionRegistry = ExtensionRegistryWrapper.wrap(EXTENSION_REGISTRY);
    for (ExtensionInfo extensionInfo : extensionRegistry.findExtensionsByDescriptor(descriptor)) {
      allExtensionFields.add(translate(extensionInfo.descriptor.getName()));
      if (extensionInfo.descriptor.isRepeated()) {
        arrayExtensionFields.add(translate(extensionInfo.descriptor.getName()));
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

    for (String field : allExtensionFields) {
      assertThat(node.has(field)).isFalse();
    }
  }

  @Test
  public void itOnlyWritesArrayFieldsWhenSerializationIncludeIsNotAlways() {
    AllFields message = AllFields.getDefaultInstance();

    for (Include inclusion : EnumSet.complementOf(EXCLUDED_VALUES)) {
      JsonNode node = mapper(inclusion).valueToTree(message);

      for (String field : allFields) {
        if (arrayFields.contains(field)) {
          assertThat(node.has(field)).isTrue();
          assertThat(node.get(field).isArray());
        } else {
          assertThat(node.has(field)).isFalse();
        }
      }

      for (String field : allExtensionFields) {
        assertThat(node.has(field)).isFalse();
      }
    }
  }

  @Test
  public void itWritesMissingExtensionFieldsAsNullWhenSerializationIncludeIsAlways() {
    AllFields message = AllFields.getDefaultInstance();

    JsonNode node = mapper(Include.ALWAYS, EXTENSION_REGISTRY).valueToTree(message);

    for (String field : allExtensionFields) {
      assertThat(node.has(field)).isTrue();
      if (arrayExtensionFields.contains(field)) {
        assertThat(node.get(field).isArray());
      } else {
        assertThat(node.get(field).isNull());
      }
    }
  }

  @Test
  public void itOnlyWritesArrayExtensionFieldsWhenSerializationIncludeIsNotAlways() {
    AllFields message = AllFields.getDefaultInstance();

    for (Include inclusion : EnumSet.complementOf(EXCLUDED_VALUES)) {
      JsonNode node = mapper(inclusion, EXTENSION_REGISTRY).valueToTree(message);

      for (String field : allExtensionFields) {
        if (arrayExtensionFields.contains(field)) {
          assertThat(node.has(field)).isTrue();
          assertThat(node.get(field).isArray());
        } else {
          assertThat(node.has(field)).isFalse();
        }
      }
    }
  }

  private static String translate(String fieldName) {
    return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, fieldName);
  }

  private static ObjectMapper mapper(Include inclusion) {
    return camelCase().copy().setSerializationInclusion(inclusion);
  }

  private static ObjectMapper mapper(Include inclusion, ExtensionRegistry extensionRegistry) {
    return camelCase(extensionRegistry).copy().setSerializationInclusion(inclusion);
  }

  private static EnumSet<Include> presentValues(String... values) {
    EnumSet<Include> set = EnumSet.noneOf(Include.class);

    for (String value : values) {
      set.addAll(Enums.getIfPresent(Include.class, value).asSet());
    }

    return set;
  }
}
