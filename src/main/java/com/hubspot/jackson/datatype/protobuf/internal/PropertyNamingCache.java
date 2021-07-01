package com.hubspot.jackson.datatype.protobuf.internal;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Function;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.PropertyNamingStrategyBase;
import com.google.common.collect.ImmutableMap;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.hubspot.jackson.datatype.protobuf.PropertyNamingStrategyWrapper;
import com.hubspot.jackson.datatype.protobuf.ProtobufJacksonConfig;

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
    return field.toProto().hasJsonName()
        ? field.getJsonName()
        : namingStrategy.translate(field.getName());
  }
}
