package com.hubspot.jackson.datatype.protobuf.builtin.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.MessageOrBuilder;
import com.hubspot.jackson.datatype.protobuf.ProtobufJacksonConfig;
import com.hubspot.jackson.datatype.protobuf.ProtobufSerializer;
import java.io.IOException;

public class WrappedPrimitiveSerializer<T extends MessageOrBuilder> extends ProtobufSerializer<T> {

  /**
   * @deprecated use {@link #WrappedPrimitiveSerializer(Class, ProtobufJacksonConfig)}
   */
  @Deprecated
  public WrappedPrimitiveSerializer(Class<T> wrapperType) {
    this(wrapperType, ProtobufJacksonConfig.getDefaultInstance());
  }

  public WrappedPrimitiveSerializer(Class<T> wrapperType, ProtobufJacksonConfig config) {
    super(wrapperType, config);
  }

  @Override
  public void serialize(
          MessageOrBuilder message,
          JsonGenerator generator,
          SerializerProvider serializerProvider
  ) throws IOException {
    FieldDescriptor field = message.getDescriptorForType().findFieldByName("value");
    Object value = message.getField(field);
    writeValue(field, value, generator, serializerProvider);
  }
}
