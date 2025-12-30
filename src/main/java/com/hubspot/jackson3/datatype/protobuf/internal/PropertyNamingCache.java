package com.hubspot.jackson3.datatype.protobuf.internal;

import com.google.common.collect.ImmutableMap;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Message;
import com.hubspot.jackson3.datatype.protobuf.PropertyNamingStrategyWrapper;
import com.hubspot.jackson3.datatype.protobuf.ProtobufJacksonConfig;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Function;
import tools.jackson.databind.PropertyNamingStrategy;
import tools.jackson.databind.cfg.MapperConfig;

public class PropertyNamingCache {

  private final Descriptor descriptor;
  private final Class<? extends Message> messageType;
  private final ProtobufJacksonConfig config;
  private final Map<PropertyNamingStrategy, Function<FieldDescriptor, String>> serializationCache;
  private final Map<PropertyNamingStrategy, Function<String, FieldDescriptor>> deserializationCache;

  private PropertyNamingCache(
    Descriptor descriptor,
    Class<? extends Message> messageType,
    ProtobufJacksonConfig config
  ) {
    this.descriptor = descriptor;
    this.messageType = messageType;
    this.config = config;
    this.serializationCache = Collections.synchronizedMap(new WeakHashMap<>());
    this.deserializationCache = Collections.synchronizedMap(new WeakHashMap<>());
  }

  public static PropertyNamingCache forDescriptor(
    Descriptor descriptor,
    Class<? extends Message> messageType,
    ProtobufJacksonConfig config
  ) {
    return new PropertyNamingCache(descriptor, messageType, config);
  }

  public Function<FieldDescriptor, String> forSerialization(
    MapperConfig<?> mapperConfig
  ) {
    Function<FieldDescriptor, String> cached = serializationCache.get(
      mapperConfig.getPropertyNamingStrategy()
    );
    if (cached != null) {
      return cached;
    } else {
      Function<FieldDescriptor, String> function = buildSerializationFunction(
        descriptor,
        mapperConfig
      );
      serializationCache.put(mapperConfig.getPropertyNamingStrategy(), function);

      return function;
    }
  }

  public Function<String, FieldDescriptor> forDeserialization(
    MapperConfig<?> mapperConfig
  ) {
    Function<String, FieldDescriptor> cached = deserializationCache.get(
      mapperConfig.getPropertyNamingStrategy()
    );
    if (cached != null) {
      return cached;
    } else {
      Function<String, FieldDescriptor> function = buildDeserializationFunction(
        descriptor,
        mapperConfig
      );
      deserializationCache.put(mapperConfig.getPropertyNamingStrategy(), function);

      return function;
    }
  }

  private Function<FieldDescriptor, String> buildSerializationFunction(
    Descriptor descriptor,
    MapperConfig<?> mapperConfig
  ) {
    PropertyNamingStrategyWrapper namingStrategy = new PropertyNamingStrategyWrapper(
      messageType,
      mapperConfig
    );

    Map<FieldDescriptor, String> tempMap = new HashMap<>();
    for (FieldDescriptor field : descriptor.getFields()) {
      tempMap.put(field, getFieldName(field, namingStrategy));
    }

    ImmutableMap<FieldDescriptor, String> fieldLookup = ImmutableMap.copyOf(tempMap);
    return field -> {
      String name = fieldLookup.get(field);
      return name == null ? namingStrategy.translate(field.getName()) : name;
    };
  }

  private Function<String, FieldDescriptor> buildDeserializationFunction(
    Descriptor descriptor,
    MapperConfig<?> mapperConfig
  ) {
    PropertyNamingStrategyWrapper namingStrategy = new PropertyNamingStrategyWrapper(
      messageType,
      mapperConfig
    );

    Map<String, FieldDescriptor> tempMap = new HashMap<>();
    for (FieldDescriptor field : descriptor.getFields()) {
      tempMap.put(getFieldName(field, namingStrategy), field);
    }

    if (config.acceptLiteralFieldnames()) {
      for (FieldDescriptor field : descriptor.getFields()) {
        if (!tempMap.containsKey(field.getName())) {
          tempMap.put(field.getName(), field);
        }
      }
    }

    Map<String, FieldDescriptor> fieldLookup = ImmutableMap.copyOf(tempMap);
    return fieldLookup::get;
  }

  private static String getFieldName(
    FieldDescriptor field,
    PropertyNamingStrategyWrapper namingStrategy
  ) {
    return hasJsonName(field)
      ? field.getJsonName()
      : namingStrategy.translate(field.getName());
  }

  private static boolean hasJsonName(FieldDescriptor field) {
    if (!field.toProto().hasJsonName()) {
      return false;
    } else {
      return !field.getJsonName().equals(defaultJsonName(field.getName()));
    }
  }

  /**
   * Copied from {@link com.google.protobuf.Descriptors} because the method is private
   */
  private static String defaultJsonName(String fieldName) {
    final int length = fieldName.length();
    StringBuilder result = new StringBuilder(length);
    boolean isNextUpperCase = false;
    for (int i = 0; i < length; i++) {
      char ch = fieldName.charAt(i);
      if (ch == '_') {
        isNextUpperCase = true;
      } else if (isNextUpperCase) {
        // This closely matches the logic for ASCII characters in:
        // http://google3/google/protobuf/descriptor.cc?l=249-251&rcl=228891689
        if ('a' <= ch && ch <= 'z') {
          ch = (char) (ch - 'a' + 'A');
        }
        result.append(ch);
        isNextUpperCase = false;
      } else {
        result.append(ch);
      }
    }
    return result.toString();
  }
}
