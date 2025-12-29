package com.hubspot.jackson.datatype.protobuf;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.google.protobuf.BoolValue;
import com.google.protobuf.BytesValue;
import com.google.protobuf.DoubleValue;
import com.google.protobuf.Duration;
import com.google.protobuf.FieldMask;
import com.google.protobuf.FloatValue;
import com.google.protobuf.Int32Value;
import com.google.protobuf.Int64Value;
import com.google.protobuf.ListValue;
import com.google.protobuf.Message;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.NullValue;
import com.google.protobuf.StringValue;
import com.google.protobuf.Struct;
import com.google.protobuf.Timestamp;
import com.google.protobuf.UInt32Value;
import com.google.protobuf.UInt64Value;
import com.google.protobuf.Value;
import com.hubspot.jackson.datatype.protobuf.builtin.deserializers.DurationDeserializer;
import com.hubspot.jackson.datatype.protobuf.builtin.deserializers.FieldMaskDeserializer;
import com.hubspot.jackson.datatype.protobuf.builtin.deserializers.ListValueDeserializer;
import com.hubspot.jackson.datatype.protobuf.builtin.deserializers.NullValueDeserializer;
import com.hubspot.jackson.datatype.protobuf.builtin.deserializers.ProtobufValueDeserializer;
import com.hubspot.jackson.datatype.protobuf.builtin.deserializers.StructDeserializer;
import com.hubspot.jackson.datatype.protobuf.builtin.deserializers.TimestampDeserializer;
import com.hubspot.jackson.datatype.protobuf.builtin.deserializers.WrappedPrimitiveDeserializer;
import com.hubspot.jackson.datatype.protobuf.builtin.serializers.DurationSerializer;
import com.hubspot.jackson.datatype.protobuf.builtin.serializers.FieldMaskSerializer;
import com.hubspot.jackson.datatype.protobuf.builtin.serializers.ListValueSerializer;
import com.hubspot.jackson.datatype.protobuf.builtin.serializers.MessageSerializer;
import com.hubspot.jackson.datatype.protobuf.builtin.serializers.NullValueSerializer;
import com.hubspot.jackson.datatype.protobuf.builtin.serializers.ProtobufValueSerializer;
import com.hubspot.jackson.datatype.protobuf.builtin.serializers.StructSerializer;
import com.hubspot.jackson.datatype.protobuf.builtin.serializers.TimestampSerializer;
import com.hubspot.jackson.datatype.protobuf.builtin.serializers.WrappedPrimitiveSerializer;
import tools.jackson.core.Version;
import tools.jackson.databind.JacksonModule;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.module.SimpleDeserializers;
import tools.jackson.databind.module.SimpleSerializers;

/**
 * Module to add support for reading and writing Protobufs
 *
 * Register with Jackson via {@link ObjectMapper#registeredModules()}
 */
public class ProtobufModule extends JacksonModule {

  private final ProtobufJacksonConfig config;

  public ProtobufModule() {
    this(ProtobufJacksonConfig.getDefaultInstance());
  }

  public ProtobufModule(ProtobufJacksonConfig config) {
    this.config = config;
  }

  @Override
  public String getModuleName() {
    return "ProtobufModule";
  }

  @Override
  public Version version() {
    return Version.unknownVersion();
  }

  @Override
  public void setupModule(SetupContext context) {
    SimpleSerializers serializers = new SimpleSerializers();
    serializers.addSerializer(new MessageSerializer(config));
    serializers.addSerializer(new DurationSerializer(config));
    serializers.addSerializer(new FieldMaskSerializer(config));
    serializers.addSerializer(new ListValueSerializer(config));
    serializers.addSerializer(new NullValueSerializer(config));
    serializers.addSerializer(new StructSerializer(config));
    serializers.addSerializer(new TimestampSerializer(config));
    serializers.addSerializer(new ProtobufValueSerializer(config));
    serializers.addSerializer(
      new WrappedPrimitiveSerializer<>(DoubleValue.getDefaultInstance(), config)
    );
    serializers.addSerializer(
      new WrappedPrimitiveSerializer<>(FloatValue.getDefaultInstance(), config)
    );
    serializers.addSerializer(
      new WrappedPrimitiveSerializer<>(Int64Value.getDefaultInstance(), config)
    );
    serializers.addSerializer(
      new WrappedPrimitiveSerializer<>(UInt64Value.getDefaultInstance(), config)
    );
    serializers.addSerializer(
      new WrappedPrimitiveSerializer<>(Int32Value.getDefaultInstance(), config)
    );
    serializers.addSerializer(
      new WrappedPrimitiveSerializer<>(UInt32Value.getDefaultInstance(), config)
    );
    serializers.addSerializer(
      new WrappedPrimitiveSerializer<>(BoolValue.getDefaultInstance(), config)
    );
    serializers.addSerializer(
      new WrappedPrimitiveSerializer<>(StringValue.getDefaultInstance(), config)
    );
    serializers.addSerializer(
      new WrappedPrimitiveSerializer<>(BytesValue.getDefaultInstance(), config)
    );

    context.addSerializers(serializers);

    context.addDeserializers(new MessageDeserializerFactory(config));
    SimpleDeserializers deserializers = new SimpleDeserializers();
    deserializers.addDeserializer(Duration.class, new DurationDeserializer());
    deserializers.addDeserializer(FieldMask.class, new FieldMaskDeserializer());
    deserializers.addDeserializer(
      ListValue.class,
      new ListValueDeserializer().buildAtEnd()
    );
    deserializers.addDeserializer(NullValue.class, new NullValueDeserializer());
    deserializers.addDeserializer(Struct.class, new StructDeserializer().buildAtEnd());
    deserializers.addDeserializer(Timestamp.class, new TimestampDeserializer());
    deserializers.addDeserializer(
      Value.class,
      new ProtobufValueDeserializer().buildAtEnd()
    );
    deserializers.addDeserializer(
      DoubleValue.class,
      wrappedPrimitiveDeserializer(DoubleValue.class)
    );
    deserializers.addDeserializer(
      FloatValue.class,
      wrappedPrimitiveDeserializer(FloatValue.class)
    );
    deserializers.addDeserializer(
      Int64Value.class,
      wrappedPrimitiveDeserializer(Int64Value.class)
    );
    deserializers.addDeserializer(
      UInt64Value.class,
      wrappedPrimitiveDeserializer(UInt64Value.class)
    );
    deserializers.addDeserializer(
      Int32Value.class,
      wrappedPrimitiveDeserializer(Int32Value.class)
    );
    deserializers.addDeserializer(
      UInt32Value.class,
      wrappedPrimitiveDeserializer(UInt32Value.class)
    );
    deserializers.addDeserializer(
      BoolValue.class,
      wrappedPrimitiveDeserializer(BoolValue.class)
    );
    deserializers.addDeserializer(
      StringValue.class,
      wrappedPrimitiveDeserializer(StringValue.class)
    );
    deserializers.addDeserializer(
      BytesValue.class,
      wrappedPrimitiveDeserializer(BytesValue.class)
    );
    context.addDeserializers(deserializers);
    context.setMixIn(MessageOrBuilder.class, MessageOrBuilderMixin.class);
  }

  private static <T extends Message> ValueDeserializer<T> wrappedPrimitiveDeserializer(
    Class<T> type
  ) {
    return new WrappedPrimitiveDeserializer<>(type).buildAtEnd();
  }

  @JsonAutoDetect(
    getterVisibility = Visibility.NONE,
    isGetterVisibility = Visibility.NONE,
    setterVisibility = Visibility.NONE,
    creatorVisibility = Visibility.NONE,
    fieldVisibility = Visibility.NONE
  )
  private static class MessageOrBuilderMixin {}
}
