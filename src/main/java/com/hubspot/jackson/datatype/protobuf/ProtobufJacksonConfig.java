package com.hubspot.jackson.datatype.protobuf;

import com.google.protobuf.ExtensionRegistry;

public class ProtobufJacksonConfig {
  private final ExtensionRegistryWrapper extensionRegistry;
  private final boolean acceptLiteralFieldnames;
  private final UnknownEnumSerializationStrategy unknownEnumSerializationStrategy;

  private ProtobufJacksonConfig(ExtensionRegistryWrapper extensionRegistry, boolean acceptLiteralFieldnames,
      UnknownEnumSerializationStrategy unknownEnumSerializationStrategy) {
    this.extensionRegistry = extensionRegistry;
    this.acceptLiteralFieldnames = acceptLiteralFieldnames;
    this.unknownEnumSerializationStrategy = unknownEnumSerializationStrategy;
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

  public UnknownEnumSerializationStrategy unknownEnumSerializationStrategy() {
    return unknownEnumSerializationStrategy;
  }

  public static class Builder {
    private ExtensionRegistryWrapper extensionRegistry = ExtensionRegistryWrapper.empty();
    private boolean acceptLiteralFieldnames = false;
    private UnknownEnumSerializationStrategy unknownEnumSerializationStrategy = UnknownEnumSerializationStrategy.SERIALIZE;

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

    public Builder unknownEnumSerializationStrategy(UnknownEnumSerializationStrategy unknownEnumSerializationStrategy) {
      this.unknownEnumSerializationStrategy = unknownEnumSerializationStrategy;
      return this;
    }

    public ProtobufJacksonConfig build() {
      return new ProtobufJacksonConfig(extensionRegistry, acceptLiteralFieldnames, unknownEnumSerializationStrategy);
    }
  }
}
