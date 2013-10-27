package com.hubspot.jackson.datatype.protobuf;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.Deserializers;
import com.google.common.base.CaseFormat;
import com.google.protobuf.Message;

public class ProtobufDeserializerFactory extends Deserializers.Base {
  private final CaseFormat format;

  public ProtobufDeserializerFactory(CaseFormat format) {
    this.format = format;
  }

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

  private <T extends Message> ProtobufDeserializer<T> getDeserializer(Class<T> messageType, boolean build)
          throws JsonMappingException{
    return new ProtobufDeserializer<T>(messageType, format, build);
  }
}
