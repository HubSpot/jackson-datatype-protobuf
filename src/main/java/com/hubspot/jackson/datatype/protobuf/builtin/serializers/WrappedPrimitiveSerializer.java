package com.hubspot.jackson.datatype.protobuf.builtin.serializers;

import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.MessageOrBuilder;
import com.hubspot.jackson.datatype.protobuf.ProtobufJacksonConfig;
import com.hubspot.jackson.datatype.protobuf.ProtobufSerializer;
import com.hubspot.jackson.datatype.protobuf.internal.FieldSchemaGenerator;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;

public class WrappedPrimitiveSerializer<T extends MessageOrBuilder>
  extends ProtobufSerializer<T> {

  private final FieldDescriptor valueField;

  /**
   * @deprecated use {@link #WrappedPrimitiveSerializer(Class, ProtobufJacksonConfig)}
   */
  @Deprecated
  public WrappedPrimitiveSerializer(Class<T> wrapperType) {
    this(wrapperType, ProtobufJacksonConfig.getDefaultInstance());
  }

  /**
   * @deprecated use {@link #WrappedPrimitiveSerializer(T, ProtobufJacksonConfig)}
   */
  @Deprecated
  public WrappedPrimitiveSerializer(Class<T> wrapperType, ProtobufJacksonConfig config) {
    this(defaultInstance(wrapperType), config);
  }

  @SuppressWarnings("unchecked")
  public WrappedPrimitiveSerializer(T defaultInstance, ProtobufJacksonConfig config) {
    super((Class<T>) defaultInstance.getClass(), config);
    this.valueField = defaultInstance.getDescriptorForType().findFieldByName("value");
  }

  @Override
  public void serialize(
    MessageOrBuilder message,
    JsonGenerator generator,
    SerializationContext serializerProvider
  ) throws JacksonException {
    Object value = message.getField(valueField);
    writeValue(valueField, value, generator, serializerProvider);
  }

  @Override
  public void acceptJsonFormatVisitor(
    JsonFormatVisitorWrapper visitor,
    JavaType typeHint
  ) {
    new FieldSchemaGenerator(valueField, getConfig())
      .acceptJsonFormatVisitor(visitor, typeHint);
  }

  @SuppressWarnings("unchecked")
  private static <T> T defaultInstance(Class<T> type) {
    try {
      return (T) type.getMethod("getDefaultInstance").invoke(null);
    } catch (Exception e) {
      throw new RuntimeException("Unable to get default instance for type " + type, e);
    }
  }
}
