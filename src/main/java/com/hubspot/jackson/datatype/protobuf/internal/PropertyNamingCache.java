package com.hubspot.jackson.datatype.protobuf.internal;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.PropertyNamingStrategyBase;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.MapMaker;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.hubspot.jackson.datatype.protobuf.PropertyNamingStrategyWrapper;
import com.hubspot.jackson.datatype.protobuf.ProtobufJacksonConfig;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

public class PropertyNamingCache {

  private static final int DEFAULT_CONCURRENCY_LEVEL = Math.min(Runtime.getRuntime().availableProcessors(), 16);
  private final Descriptor descriptor;
  private final ProtobufJacksonConfig config;
  private final Map<PropertyNamingStrategy, Function<FieldDescriptor, String>> serializationCache;
  private final Map<PropertyNamingStrategy, Function<String, FieldDescriptor>> deserializationCache;

  private static <K, V> ConcurrentMap<K, V> buildWeakMap() {
    return new MapMaker().concurrencyLevel(DEFAULT_CONCURRENCY_LEVEL).weakKeys().makeMap();
  }

  private PropertyNamingCache(Descriptor descriptor, ProtobufJacksonConfig config) {
    this.descriptor = descriptor;
    this.config = config;
    this.serializationCache = buildWeakMap();
    this.deserializationCache = buildWeakMap();
  }

  public static PropertyNamingCache forDescriptor(Descriptor descriptor, ProtobufJacksonConfig config) {
    return new PropertyNamingCache(descriptor, config);
  }

  public Function<FieldDescriptor, String> forSerialization(PropertyNamingStrategy propertyNamingStrategy) {
    return serializationCache.computeIfAbsent(propertyNamingStrategy, this::buildSerializationFunction);
  }

  public Function<String, FieldDescriptor> forDeserialization(PropertyNamingStrategy propertyNamingStrategy) {
    return deserializationCache.computeIfAbsent(propertyNamingStrategy,this::buildDeserializationFunction);
  }

  private Function<FieldDescriptor, String> buildSerializationFunction(
      PropertyNamingStrategy originalNamingStrategy
  ) {
    PropertyNamingStrategyBase namingStrategy =
        new PropertyNamingStrategyWrapper(originalNamingStrategy);

    Map<FieldDescriptor, String> tempMap = new HashMap<>();
    for (FieldDescriptor field : descriptor.getFields()) {
      tempMap.put(field, namingStrategy.translate(field.getName()));
    }

    ImmutableMap<FieldDescriptor, String> fieldLookup = ImmutableMap.copyOf(tempMap);
    return field -> {
      String name = fieldLookup.get(field);
      return name == null ? namingStrategy.translate(field.getName()) : name;
    };
  }

  private Function<String, FieldDescriptor> buildDeserializationFunction(
      PropertyNamingStrategy originalNamingStrategy
  ) {
    PropertyNamingStrategyBase namingStrategy =
        new PropertyNamingStrategyWrapper(originalNamingStrategy);

    Map<String, FieldDescriptor> tempMap = new HashMap<>();
    for (FieldDescriptor field : descriptor.getFields()) {
      tempMap.put(namingStrategy.translate(field.getName()), field);
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
}
