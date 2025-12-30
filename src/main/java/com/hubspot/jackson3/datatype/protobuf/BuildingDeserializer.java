package com.hubspot.jackson3.datatype.protobuf;

import com.google.protobuf.Message;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.deser.std.StdDeserializer;

public abstract class BuildingDeserializer<T extends Message, V extends Message.Builder>
  extends StdDeserializer<T> {

  protected BuildingDeserializer(Class<T> messageType) {
    super(messageType);
  }

  public abstract ValueDeserializer<V> getWrappedDeserializer();

  @Override
  @SuppressWarnings("unchecked")
  public T deserialize(JsonParser parser, DeserializationContext context) {
    return (T) getWrappedDeserializer().deserialize(parser, context).build();
  }

  @Override
  @SuppressWarnings("unchecked")
  public T getNullValue(DeserializationContext context) {
    Message.Builder builder = (Message.Builder) getWrappedDeserializer()
      .getNullValue(context);
    return builder == null ? null : (T) builder.build();
  }
}
