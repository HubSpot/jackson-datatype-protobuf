package com.hubspot.jackson.datatype.protobuf.internal;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.PropertyNamingStrategyBase;
import com.google.common.collect.ImmutableMap;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.hubspot.jackson.datatype.protobuf.PropertyNamingStrategyWrapper;
import com.hubspot.jackson.datatype.protobuf.ProtobufJacksonConfig;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Function;

public class PropertyNamingCache {
  private final Descriptor descriptor;
  private final ProtobufJacksonConfig config;
  private final Map<PropertyNamingStrategy, Function<FieldDescriptor, String>> serializationCache;
  private final Map<PropertyNamingStrategy, Function<String, FieldDescriptor>> deserializationCache;

  private PropertyNamingCache(Descriptor descriptor, ProtobufJacksonConfig config) {
    this.descriptor = descriptor;
    this.config = config;
    this.serializationCache = Collections.synchronizedMap(new WeakHashMap<>());
    this.deserializationCache = Collections.synchronizedMap(new WeakHashMap<>());
  }

  public static PropertyNamingCache forDescriptor(Descriptor descriptor, ProtobufJacksonConfig config) {
    return new PropertyNamingCache(descriptor, config);
  }

  public Function<FieldDescriptor, String> forSerialization(PropertyNamingStrategy propertyNamingStrategy) {
    Function<FieldDescriptor, String> cached = serializationCache.get(propertyNamingStrategy);
    if (cached != null) {
      return cached;
    } else {
      Function<FieldDescriptor, String> function = buildSerializationFunction(descriptor, propertyNamingStrategy);
      serializationCache.put(propertyNamingStrategy, function);

      return function;
    }
  }

  public Function<String, FieldDescriptor> forDeserialization(PropertyNamingStrategy propertyNamingStrategy) {
    Function<String, FieldDescriptor> cached = deserializationCache.get(propertyNamingStrategy);
    if (cached != null) {
      return cached;
    } else {
      Function<String, FieldDescriptor> function = buildDeserializationFunction(descriptor, propertyNamingStrategy);
      deserializationCache.put(propertyNamingStrategy, function);

      return function;
    }
  }

  private Function<FieldDescriptor, String> buildSerializationFunction(
      Descriptor descriptor,
      PropertyNamingStrategy originalNamingStrategy
  ) {
    PropertyNamingStrategyBase namingStrategy =
        new PropertyNamingStrategyWrapper(originalNamingStrategy);

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
      PropertyNamingStrategy originalNamingStrategy
  ) {
    PropertyNamingStrategyBase namingStrategy =
        new PropertyNamingStrategyWrapper(originalNamingStrategy);

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
      FieldDescriptor field, PropertyNamingStrategyBase namingStrategy) {
    return hasJsonName(field)
        ? field.getJsonName()
        : namingStrategy.translate(field.getName());
  }

  private static boolean hasJsonName(FieldDescriptor field) {
    if (!field.toProto().hasJsonName()) {
      return false;
    } else {
      return !matchesDefaultJsonName(field.getJsonName(), field.getName());
    }
  }

  /**
   * Copied from {@link com.google.protobuf.Descriptors} because the method is private, then adapted to return a boolean
   * as soon as possible without a need to grow a StringBuilder
   */
  private static boolean matchesDefaultJsonName(String jsonName, String fieldName) {
    int defaultLength = 0;
    final int length = fieldName.length();
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
        if (ch != jsonName.charAt(defaultLength++)) {
          return false;
        }
        isNextUpperCase = false;
      } else if (ch != jsonName.charAt(defaultLength++)) {
        return false;
      }
    }
    return defaultLength == jsonName.length();
  }
}
