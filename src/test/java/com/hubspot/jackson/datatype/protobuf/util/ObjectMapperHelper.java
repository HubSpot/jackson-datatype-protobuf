package com.hubspot.jackson.datatype.protobuf.util;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.google.common.collect.Lists;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.MessageOrBuilder;
import com.hubspot.jackson.datatype.protobuf.ProtobufJacksonConfig;
import com.hubspot.jackson.datatype.protobuf.ProtobufModule;

public class ObjectMapperHelper {
  private static final ObjectMapper DEFAULT = create();
  private static final ObjectMapper UNDERSCORE = create(PropertyNamingStrategy.SNAKE_CASE);

  public static ObjectMapper camelCase() {
    return DEFAULT;
  }

  public static ObjectMapper underscore() {
    return UNDERSCORE;
  }

  public static ObjectMapper camelCase(Include inclusion) {
    return create().setSerializationInclusion(inclusion);
  }

  public static ObjectMapper camelCase(ExtensionRegistry extensionRegistry) {
    return create(extensionRegistry);
  }

  public static ObjectMapper underscore(ExtensionRegistry extensionRegistry) {
    return create(PropertyNamingStrategy.SNAKE_CASE, extensionRegistry);
  }

  public static ObjectMapper serializeLongsAsStrings(){
    return create(true).setSerializationInclusion(Include.NON_DEFAULT);
  }

  public static JsonNode toTree(ObjectMapper mapper, Object value) {
    return mapper.valueToTree(value);
  }

  @SuppressWarnings("unchecked")
  public static <T extends MessageOrBuilder> T writeAndReadBack(ObjectMapper mapper, T value) {
    TreeNode tree = toTree(mapper, value);

    try {
      return (T) mapper.treeToValue(tree, value.getClass());
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  @SuppressWarnings("unchecked")
  public static <T extends MessageOrBuilder> List<T> writeAndReadBack(ObjectMapper mapper, List<T> values) {
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

  private static ObjectMapper create(PropertyNamingStrategy namingStrategy, ExtensionRegistry extensionRegistry) {
    return create(extensionRegistry).setPropertyNamingStrategy(namingStrategy);
  }

  private static ObjectMapper create(ExtensionRegistry extensionRegistry) {
    return new ObjectMapper().registerModule(new ProtobufModule(extensionRegistry));
  }

  private static ObjectMapper create(PropertyNamingStrategy namingStrategy) {
    return create().setPropertyNamingStrategy(namingStrategy);
  }

  private static ObjectMapper create() {
    return new ObjectMapper().registerModule(new ProtobufModule());
  }

  private static ObjectMapper create(boolean serializeLongsAsStrings) {
    ProtobufJacksonConfig protobufJacksonConfig = ProtobufJacksonConfig.builder().serializeLongsAsStrings(serializeLongsAsStrings).build();
    ProtobufModule module = new ProtobufModule(protobufJacksonConfig);
    return new ObjectMapper().registerModule(module);
  }
}
