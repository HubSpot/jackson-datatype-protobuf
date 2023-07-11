package com.hubspot.jackson.datatype.protobuf;

import com.google.protobuf.ExtensionRegistry;

public class ProtobufJacksonConfig {
  private static final ProtobufJacksonConfig DEFAULT_INSTANCE = ProtobufJacksonConfig.builder().build();

  private final ExtensionRegistryWrapper extensionRegistry;
  private final boolean acceptLiteralFieldnames;
  private final boolean properUnsignedNumberSerialization;

  private ProtobufJacksonConfig(
    ExtensionRegistryWrapper extensionRegistry,
    boolean acceptLiteralFieldnames,
    boolean properUnsignedNumberSerialization
  ) {
    this.extensionRegistry = extensionRegistry;
    this.acceptLiteralFieldnames = acceptLiteralFieldnames;
    this.properUnsignedNumberSerialization = properUnsignedNumberSerialization;
  }

  public static ProtobufJacksonConfig getDefaultInstance() {
    return DEFAULT_INSTANCE;
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

  public boolean properUnsignedNumberSerialization() {
    return properUnsignedNumberSerialization;
  }

  public static class Builder {
    private ExtensionRegistryWrapper extensionRegistry = ExtensionRegistryWrapper.empty();
    private boolean acceptLiteralFieldnames = false;
    private boolean properUnsignedNumberSerialization = false;

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

    public Builder properUnsignedNumberSerialization(boolean properUnsignedNumberSerialization) {
      this.properUnsignedNumberSerialization = properUnsignedNumberSerialization;
      return this;
    }

    public ProtobufJacksonConfig build() {
      return new ProtobufJacksonConfig(
        extensionRegistry,
        acceptLiteralFieldnames,
        properUnsignedNumberSerialization
      );
    }
  }
}
