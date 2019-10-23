package com.hubspot.jackson.datatype.protobuf;

import java.util.Optional;
import java.util.function.Consumer;

import com.google.protobuf.ExtensionRegistry;

public class ProtobufJacksonConfig {
  private final ExtensionRegistryWrapper extensionRegistry;
  private final boolean acceptLiteralFieldnames;
  private final Optional<Consumer<Class<?>>> enumConsumer;


  private ProtobufJacksonConfig(ExtensionRegistryWrapper extensionRegistry, boolean acceptLiteralFieldnames, Optional<Consumer<Class<?>>> enumConsumer) {
    this.extensionRegistry = extensionRegistry;
    this.acceptLiteralFieldnames = acceptLiteralFieldnames;
    this.enumConsumer = enumConsumer;
  }

  public static Builder builder() {
    return new Builder();
  }

  public ExtensionRegistryWrapper extensionRegistry() {
    return extensionRegistry;
  }

  public boolean acceptLiteralFieldnames() {
    return acceptLiteralFieldnames;
  }

  public Optional<Consumer<Class<?>>> getEnumProcessingHook() {
    return enumConsumer;
  }

  public static class Builder {
    private ExtensionRegistryWrapper extensionRegistry = ExtensionRegistryWrapper.empty();
    private boolean acceptLiteralFieldnames = false;
    private Optional<Consumer<Class<?>>> enumConsumer = Optional.empty();

    private Builder() {}

    public Builder extensionRegistry(ExtensionRegistry extensionRegistry) {
      return extensionRegistry(ExtensionRegistryWrapper.wrap(extensionRegistry));
    }

    public Builder extensionRegistry(ExtensionRegistryWrapper extensionRegistry) {
      this.extensionRegistry = extensionRegistry;
      return this;
    }

    public Builder acceptLiteralFieldnames(boolean acceptLiteralFieldnames) {
      this.acceptLiteralFieldnames = acceptLiteralFieldnames;
      return this;
    }

    public Builder addEnumClassProcessingHook(Consumer<Class<?>> enumConsumer) {
      this.enumConsumer = Optional.of(enumConsumer);
      return this;
    }

    public ProtobufJacksonConfig build() {
      return new ProtobufJacksonConfig(extensionRegistry, acceptLiteralFieldnames, enumConsumer);
    }
  }
}
