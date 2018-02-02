package com.hubspot.jackson.datatype.protobuf;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.Deserializers;
import com.google.protobuf.Message;
import com.hubspot.jackson.datatype.protobuf.builtin.deserializers.MessageDeserializer;

public class ProtobufDeserializerFactory extends Deserializers.Base {
  private final ExtensionRegistryWrapper extensionRegistry;
  private final ConcurrentMap<Class<? extends Message>, ProtobufDeserializer<?>> deserializerCache;

  public ProtobufDeserializerFactory(ExtensionRegistryWrapper extensionRegistry) {
    this.extensionRegistry = extensionRegistry;
    this.deserializerCache = new ConcurrentHashMap<>();
  }

  @Override
  @SuppressWarnings("unchecked")
  public JsonDeserializer<?> findBeanDeserializer(JavaType type, DeserializationConfig config, BeanDescription beanDesc)
          throws JsonMappingException {
    if (Message.class.isAssignableFrom(type.getRawClass())) {
      return getDeserializer((Class<? extends Message>) type.getRawClass()).buildAtEnd();
    } else if (Message.Builder.class.isAssignableFrom(type.getRawClass())) {
      return getDeserializer((Class<? extends Message>) type.getRawClass().getDeclaringClass());
    } else {
      return super.findBeanDeserializer(type, config, beanDesc);
    }
  }

  @SuppressWarnings("unchecked")
  private <T extends Message> ProtobufDeserializer<T> getDeserializer(Class<T> messageType) {
    ProtobufDeserializer<?> deserializer = deserializerCache.get(messageType);
    if (deserializer == null) {
      ProtobufDeserializer<T> newDeserializer = new MessageDeserializer<>(messageType, extensionRegistry);
      ProtobufDeserializer<?> previousDeserializer = deserializerCache.putIfAbsent(messageType, newDeserializer);
      deserializer = previousDeserializer == null ? newDeserializer : previousDeserializer;
    }

    return (ProtobufDeserializer<T>) deserializer;
  }
}
