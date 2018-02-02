package com.hubspot.jackson.datatype.protobuf;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleDeserializers;
import com.fasterxml.jackson.databind.module.SimpleSerializers;
import com.google.protobuf.BoolValue;
import com.google.protobuf.BytesValue;
import com.google.protobuf.DoubleValue;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.FloatValue;
import com.google.protobuf.Int32Value;
import com.google.protobuf.Int64Value;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.StringValue;
import com.google.protobuf.UInt32Value;
import com.google.protobuf.UInt64Value;
import com.hubspot.jackson.datatype.protobuf.builtin.serializers.AnySerializer;
import com.hubspot.jackson.datatype.protobuf.builtin.serializers.DurationSerializer;
import com.hubspot.jackson.datatype.protobuf.builtin.serializers.FieldMaskSerializer;
import com.hubspot.jackson.datatype.protobuf.builtin.serializers.ListValueSerializer;
import com.hubspot.jackson.datatype.protobuf.builtin.serializers.MessageSerializer;
import com.hubspot.jackson.datatype.protobuf.builtin.serializers.NullValueSerializer;
import com.hubspot.jackson.datatype.protobuf.builtin.serializers.StructSerializer;
import com.hubspot.jackson.datatype.protobuf.builtin.serializers.TimestampSerializer;
import com.hubspot.jackson.datatype.protobuf.builtin.serializers.ValueSerializer;
import com.hubspot.jackson.datatype.protobuf.builtin.serializers.WrappedPrimitiveSerializer;

/**
 * Module to add support for reading and writing Protobufs
 *
 * Register with Jackson via {@link com.fasterxml.jackson.databind.ObjectMapper#registerModule}
 */
public class ProtobufModule extends Module {
  private final ExtensionRegistryWrapper extensionRegistry;

  public ProtobufModule() {
    this.extensionRegistry = ExtensionRegistryWrapper.empty();
  }

  public ProtobufModule(ExtensionRegistry extensionRegistry) {
    this.extensionRegistry = ExtensionRegistryWrapper.wrap(extensionRegistry);
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
    serializers.addSerializer(new MessageSerializer(extensionRegistry));
    serializers.addSerializer(new AnySerializer());
    serializers.addSerializer(new DurationSerializer());
    serializers.addSerializer(new FieldMaskSerializer());
    serializers.addSerializer(new ListValueSerializer());
    serializers.addSerializer(new NullValueSerializer());
    serializers.addSerializer(new StructSerializer());
    serializers.addSerializer(new TimestampSerializer());
    serializers.addSerializer(new ValueSerializer());
    serializers.addSerializer(new WrappedPrimitiveSerializer<>(DoubleValue.class));
    serializers.addSerializer(new WrappedPrimitiveSerializer<>(FloatValue.class));
    serializers.addSerializer(new WrappedPrimitiveSerializer<>(Int64Value.class));
    serializers.addSerializer(new WrappedPrimitiveSerializer<>(UInt64Value.class));
    serializers.addSerializer(new WrappedPrimitiveSerializer<>(Int32Value.class));
    serializers.addSerializer(new WrappedPrimitiveSerializer<>(UInt32Value.class));
    serializers.addSerializer(new WrappedPrimitiveSerializer<>(BoolValue.class));
    serializers.addSerializer(new WrappedPrimitiveSerializer<>(StringValue.class));
    serializers.addSerializer(new WrappedPrimitiveSerializer<>(BytesValue.class));

    context.addSerializers(serializers);

    SimpleDeserializers deserializers = new SimpleDeserializers();
    context.addDeserializers(deserializers);
    context.addDeserializers(new ProtobufDeserializerFactory(extensionRegistry));
    context.setMixInAnnotations(MessageOrBuilder.class, MessageOrBuilderMixin.class);
  }

  @JsonAutoDetect(getterVisibility = Visibility.NONE,
                  isGetterVisibility =  Visibility.NONE,
                  setterVisibility =  Visibility.NONE,
                  creatorVisibility = Visibility.NONE,
                  fieldVisibility = Visibility.NONE)
  private static class MessageOrBuilderMixin { }
}
