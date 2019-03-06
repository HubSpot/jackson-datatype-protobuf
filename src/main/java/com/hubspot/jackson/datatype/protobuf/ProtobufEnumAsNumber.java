package com.hubspot.jackson.datatype.protobuf;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.google.common.base.Preconditions;
import com.google.protobuf.ProtocolMessageEnum;

public final class ProtobufEnumAsNumber {

  private ProtobufEnumAsNumber() {}

  public static class Serializer extends StdSerializer<ProtocolMessageEnum> {

    protected Serializer() {
      super(ProtocolMessageEnum.class);
    }

    @Override
    public void serialize(ProtocolMessageEnum protocolMessageEnum, JsonGenerator jsonGenerator, SerializerProvider provider) throws IOException {
      jsonGenerator.writeNumber(protocolMessageEnum.getNumber());
    }
  }

  public static class Deserializer extends StdDeserializer<ProtocolMessageEnum> implements ContextualDeserializer {

    private final JavaType javaType;

    protected Deserializer() {
      this(null);
    }

    protected Deserializer(JavaType javaType) {
      super(ProtocolMessageEnum.class);
      this.javaType = javaType;
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext context, BeanProperty beanProperty) {
      // beanProperty is null when the type to deserialize is the top-level type or a generic type, not a type of a bean property
      JavaType javaType = context.getContextualType();

      if (javaType == null) {
        javaType = beanProperty.getMember().getType();
      }

      return new Deserializer(javaType);
    }

    @Override
    public ProtocolMessageEnum deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException {
      int intValue = jsonParser.getIntValue();
      Class<?> enumClass = Preconditions.checkNotNull(javaType, "javaType").getRawClass();
      try {
        return (ProtocolMessageEnum) enumClass.getMethod("forNumber", int.class).invoke(null, intValue);
      } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
        context.reportWrongTokenException(ProtocolMessageEnum.class, JsonToken.VALUE_NUMBER_INT, wrongTokenMessage(context));
        // the previous method should have thrown
        throw new AssertionError();
      }
    }
  }

  // TODO share this?
  private static String wrongTokenMessage(DeserializationContext context) {
    return "Can not deserialize instance of com.google.protobuf.ProtocolMessageEnum out of " + context.getParser().currentToken() + " token";
  }
}
