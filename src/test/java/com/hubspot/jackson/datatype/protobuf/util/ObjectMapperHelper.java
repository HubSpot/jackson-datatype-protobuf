package com.hubspot.jackson.datatype.protobuf.util;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.google.common.collect.Lists;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.MessageOrBuilder;
import com.hubspot.jackson.datatype.protobuf.ProtobufModule;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class ObjectMapperHelper {

  private static final ObjectMapper DEFAULT = create();
  private static final ObjectMapper OLD_UNDERSCORE = create(
    PropertyNamingStrategy.SNAKE_CASE
  );
  private static final ObjectMapper NEW_UNDERSCORE = create(newUnderscoreStrategy());

  public static ObjectMapper create() {
    return new ObjectMapper().registerModule(new ProtobufModule());
  }

  public static ObjectMapper camelCase() {
    return DEFAULT;
  }

  public static ObjectMapper oldUnderscore() {
    return OLD_UNDERSCORE;
  }

  public static ObjectMapper newUnderscore() {
    return NEW_UNDERSCORE;
  }

  public static ObjectMapper camelCase(Include inclusion) {
    return create().setSerializationInclusion(inclusion);
  }

  public static ObjectMapper camelCase(ExtensionRegistry extensionRegistry) {
    return create(extensionRegistry);
  }

  public static ObjectMapper oldUnderscore(ExtensionRegistry extensionRegistry) {
    return create(PropertyNamingStrategy.SNAKE_CASE, extensionRegistry);
  }

  public static ObjectMapper newUnderscore(ExtensionRegistry extensionRegistry) {
    return create(newUnderscoreStrategy(), extensionRegistry);
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
    } catch (JsonProcessingException e) {
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
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static ObjectMapper create(
    PropertyNamingStrategy namingStrategy,
    ExtensionRegistry extensionRegistry
  ) {
    return create(extensionRegistry).setPropertyNamingStrategy(namingStrategy);
  }

  private static ObjectMapper create(ExtensionRegistry extensionRegistry) {
    return new ObjectMapper().registerModule(new ProtobufModule(extensionRegistry));
  }

  private static ObjectMapper create(PropertyNamingStrategy namingStrategy) {
    return create().setPropertyNamingStrategy(namingStrategy);
  }

  private static PropertyNamingStrategy newUnderscoreStrategy() {
    try {
      return PropertyNamingStrategies.SNAKE_CASE;
    } catch (Throwable t) {
      return PropertyNamingStrategy.SNAKE_CASE;
    }
  }
}
