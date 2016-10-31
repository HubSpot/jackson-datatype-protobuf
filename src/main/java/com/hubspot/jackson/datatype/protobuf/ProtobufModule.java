package com.hubspot.jackson.datatype.protobuf;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleSerializers;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.MessageOrBuilder;

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
    return ModuleVersion.instance.version();
  }

  @Override
  public void setupModule(SetupContext context) {
    SimpleSerializers serializers = new SimpleSerializers();
    serializers.addSerializer(new ProtobufSerializer(extensionRegistry));

    context.addSerializers(serializers);
    context.addDeserializers(new ProtobufDeserializerFactory(extensionRegistry));
    context.setMixInAnnotations(MessageOrBuilder.class, MessageOrBuilderMixin.class);
    context.setClassIntrospector(new ProtobufClassIntrospector());
  }

  @JsonAutoDetect(getterVisibility = Visibility.NONE,
                  isGetterVisibility =  Visibility.NONE,
                  setterVisibility =  Visibility.NONE,
                  creatorVisibility = Visibility.NONE,
                  fieldVisibility = Visibility.NONE)
  private static class MessageOrBuilderMixin { }
}
