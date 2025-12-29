package com.hubspot.jackson.datatype.protobuf;

import com.google.protobuf.Message;
import com.hubspot.jackson.datatype.protobuf.builtin.deserializers.MessageDeserializer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import tools.jackson.databind.BeanDescription;
import tools.jackson.databind.DeserializationConfig;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.deser.Deserializers;

public class MessageDeserializerFactory extends Deserializers.Base {

  private final ProtobufJacksonConfig config;
  private final ConcurrentMap<Class<? extends Message>, ProtobufDeserializer<?, ?>> deserializerCache;

  public MessageDeserializerFactory(ProtobufJacksonConfig config) {
    this.config = config;
    this.deserializerCache = new ConcurrentHashMap<>();
  }

  @Override
  public boolean hasDeserializerFor(DeserializationConfig config, Class<?> valueType) {
    return (
      Message.class.isAssignableFrom(valueType) ||
      Message.Builder.class.isAssignableFrom(valueType)
    );
  }

  @Override
  @SuppressWarnings("unchecked")
  public ValueDeserializer<?> findBeanDeserializer(
    JavaType type,
    DeserializationConfig config,
    BeanDescription.Supplier beanDesc
  ) {
    if (Message.class.isAssignableFrom(type.getRawClass())) {
      return getDeserializer((Class<? extends Message>) type.getRawClass()).buildAtEnd();
    } else if (Message.Builder.class.isAssignableFrom(type.getRawClass())) {
      return getDeserializer(
        (Class<? extends Message>) type.getRawClass().getDeclaringClass()
      );
    } else {
      return super.findBeanDeserializer(type, config, beanDesc);
    }
  }

  @SuppressWarnings("unchecked")
  private <T extends Message> ProtobufDeserializer<T, ?> getDeserializer(
    Class<T> messageType
  ) {
    ProtobufDeserializer<?, ?> deserializer = deserializerCache.get(messageType);
    if (deserializer == null) {
      // use computeIfAbsent as a fallback because it allocates
      deserializer =
        deserializerCache.computeIfAbsent(
          messageType,
          ignored -> new MessageDeserializer<>(messageType, config)
        );
    }

    return (ProtobufDeserializer<T, ?>) deserializer;
  }
}
