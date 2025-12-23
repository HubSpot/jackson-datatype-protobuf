package com.hubspot.jackson.datatype.protobuf.util;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.google.common.collect.Lists;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.MessageOrBuilder;
import com.hubspot.jackson.datatype.protobuf.ProtobufModule;
import java.util.Collections;
import java.util.List;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.TreeNode;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.PropertyNamingStrategy;
import tools.jackson.databind.json.JsonMapper;

public class ObjectMapperHelper {

  private static final JsonMapper DEFAULT = create();
  private static final JsonMapper UNDERSCORE = create(underscoreStrategy());

  public static JsonMapper create() {
    return JsonMapper.builder().addModule(new ProtobufModule()).build();
  }

  public static JsonMapper camelCase() {
    return DEFAULT;
  }

  public static JsonMapper underscore() {
    return UNDERSCORE;
  }

  public static JsonMapper camelCase(Include inclusion) {
    return create()
      .rebuild()
      .changeDefaultPropertyInclusion(incl -> incl.withValueInclusion(inclusion))
      .build();
  }

  public static JsonMapper camelCase(ExtensionRegistry extensionRegistry) {
    return create(extensionRegistry);
  }

  public static JsonMapper underscore(ExtensionRegistry extensionRegistry) {
    return create(underscoreStrategy(), extensionRegistry);
  }

  public static JsonNode toTree(ObjectMapper mapper, Object value) {
    return mapper.valueToTree(value);
  }

  @SuppressWarnings("unchecked")
  public static <T extends MessageOrBuilder> T writeAndReadBack(
    ObjectMapper mapper,
    T value
  ) {
    TreeNode tree = toTree(mapper, value);

    try {
      return (T) mapper.treeToValue(tree, value.getClass());
    } catch (JacksonException e) {
      throw new RuntimeException(e);
    }
  }

  @SuppressWarnings("unchecked")
  public static <T extends MessageOrBuilder> List<T> writeAndReadBack(
    ObjectMapper mapper,
    List<T> values
  ) {
    if (values.isEmpty()) {
      return Collections.emptyList();
    }

    Class<T> messageType = (Class<T>) values.get(0).getClass();
    JsonParser parser = mapper.treeAsTokens(toTree(mapper, values));

    try {
      return Lists.newArrayList(mapper.readValues(parser, messageType));
    } catch (JacksonException e) {
      throw new RuntimeException(e);
    }
  }

  private static JsonMapper create(
    PropertyNamingStrategy namingStrategy,
    ExtensionRegistry extensionRegistry
  ) {
    return create(extensionRegistry)
      .rebuild()
      .propertyNamingStrategy(namingStrategy)
      .build();
  }

  private static JsonMapper create(ExtensionRegistry extensionRegistry) {
    return JsonMapper.builder().addModule(new ProtobufModule(extensionRegistry)).build();
  }

  private static JsonMapper create(PropertyNamingStrategy namingStrategy) {
    return create().rebuild().propertyNamingStrategy(namingStrategy).build();
  }

  private static PropertyNamingStrategy underscoreStrategy() {
    return PropertyNamingStrategies.SNAKE_CASE;
  }
}
