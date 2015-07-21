package com.hubspot.jackson.datatype.protobuf;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.Deserializers;
import com.google.protobuf.Message;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ProtobufDeserializerFactory extends Deserializers.Base {
  private final ConcurrentMap<CacheKey, ProtobufDeserializer<?>> DESERIALIZER_CACHE = new ConcurrentHashMap<>();

  @Override
  @SuppressWarnings("unchecked")
  public JsonDeserializer<?> findBeanDeserializer(JavaType type, DeserializationConfig config, BeanDescription beanDesc)
          throws JsonMappingException {
    if (Message.class.isAssignableFrom(type.getRawClass())) {
      return getDeserializer((Class<? extends Message>) type.getRawClass(), true);
    } else if (Message.Builder.class.isAssignableFrom(type.getRawClass())) {
      return getDeserializer((Class<? extends Message>) type.getRawClass().getDeclaringClass(), false);
    } else {
      return super.findBeanDeserializer(type, config, beanDesc);
    }
  }

  @SuppressWarnings("unchecked")
  private <T extends Message> ProtobufDeserializer<T> getDeserializer(Class<T> messageType, boolean build)
          throws JsonMappingException {
    CacheKey cacheKey = new CacheKey(messageType, build);

    final ProtobufDeserializer<?> deserializer;
    if (DESERIALIZER_CACHE.containsKey(cacheKey)) {
      deserializer = DESERIALIZER_CACHE.get(cacheKey);
    } else {
      ProtobufDeserializer<T> newDeserializer = new ProtobufDeserializer<>(messageType, build);
      ProtobufDeserializer<?> previousDeserializer = DESERIALIZER_CACHE.putIfAbsent(cacheKey, newDeserializer);
      deserializer = previousDeserializer == null ? newDeserializer : previousDeserializer;
    }

    return (ProtobufDeserializer<T>) deserializer;
  }

  private static class CacheKey {
    private final Class<?> messageType;
    private final boolean build;

    public CacheKey(Class<?> messageType, boolean build) {
      this.messageType = messageType;
      this.build = build;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }

      if (o == null || getClass() != o.getClass()) {
        return false;
      }

      CacheKey cacheKey = (CacheKey) o;
      return Objects.equals(build, cacheKey.build) && Objects.equals(messageType, cacheKey.messageType);
    }

    @Override
    public int hashCode() {
      return Objects.hash(messageType, build);
    }
  }
}
