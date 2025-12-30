package com.hubspot.jackson3.datatype.protobuf.util;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.google.common.collect.Lists;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.MessageOrBuilder;
import com.hubspot.jackson3.datatype.protobuf.ProtobufJacksonConfig;
import com.hubspot.jackson3.datatype.protobuf.ProtobufModule;
import java.util.Collections;
import java.util.List;
import tools.jackson.core.JsonParser;
import tools.jackson.core.TreeNode;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.PropertyNamingStrategy;
import tools.jackson.databind.json.JsonMapper;

public class ObjectMapperHelper {

  private static final ObjectMapper DEFAULT = create().build();
  private static final ObjectMapper UNDERSCORE = create()
    .propertyNamingStrategy(underscoreStrategy())
    .build();

  public static JsonMapper.Builder create() {
    return create(ProtobufJacksonConfig.getDefaultInstance());
  }

  public static JsonMapper.Builder create(ProtobufJacksonConfig config) {
    return JsonMapper
      .builder()
      .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
      .addModule(new ProtobufModule(config));
  }

  public static ObjectMapper camelCase() {
    return DEFAULT;
  }

  public static ObjectMapper underscore() {
    return UNDERSCORE;
  }

  public static ObjectMapper camelCase(Include inclusion) {
    return create()
      .changeDefaultPropertyInclusion(handler -> handler.withValueInclusion(inclusion))
      .build();
  }

  public static ObjectMapper camelCase(ExtensionRegistry extensionRegistry) {
    return create(extensionRegistry).build();
  }

  public static ObjectMapper underscore(ExtensionRegistry extensionRegistry) {
    return create(underscoreStrategy(), extensionRegistry).build();
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

    return (T) mapper.treeToValue(tree, value.getClass());
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

    return Lists.newArrayList(mapper.readValues(parser, messageType));
  }

  private static JsonMapper.Builder create(
    PropertyNamingStrategy namingStrategy,
    ExtensionRegistry extensionRegistry
  ) {
    return create(extensionRegistry).propertyNamingStrategy(namingStrategy);
  }

  private static JsonMapper.Builder create(ExtensionRegistry extensionRegistry) {
    return JsonMapper
      .builder()
      .addModule(
        new ProtobufModule(
          ProtobufJacksonConfig.builder().extensionRegistry(extensionRegistry).build()
        )
      );
  }

  private static PropertyNamingStrategy underscoreStrategy() {
    return PropertyNamingStrategies.SNAKE_CASE;
  }
}
