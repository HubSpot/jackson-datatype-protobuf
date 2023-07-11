package com.hubspot.jackson.datatype.protobuf;

import com.google.protobuf.ExtensionRegistry;

public class ProtobufJacksonConfig {
  private static final ProtobufJacksonConfig DEFAULT_INSTANCE = ProtobufJacksonConfig.builder().build();

  private final ExtensionRegistryWrapper extensionRegistry;
  private final boolean acceptLiteralFieldnames;
  private final boolean properUnsignedNumberSerialization;
  private final boolean serializeLongsAsString;

  private ProtobufJacksonConfig(
    ExtensionRegistryWrapper extensionRegistry,
    boolean acceptLiteralFieldnames,
    boolean properUnsignedNumberSerialization,
    boolean serializeLongsAsString
  ) {
    this.extensionRegistry = extensionRegistry;
    this.acceptLiteralFieldnames = acceptLiteralFieldnames;
    this.properUnsignedNumberSerialization = properUnsignedNumberSerialization;
    this.serializeLongsAsString = serializeLongsAsString;
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

  public boolean serializeLongsAsString() {
    return serializeLongsAsString;
  }

  public static class Builder {
    private ExtensionRegistryWrapper extensionRegistry = ExtensionRegistryWrapper.empty();
    private boolean acceptLiteralFieldnames = false;
    private boolean properUnsignedNumberSerialization = false;
    private boolean serializeLongsAsString = false;

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

    public Builder useCanonicalSerialization() {
      properUnsignedNumberSerialization(true);
      serializeLongsAsString(true);
      return this;
    }

    public Builder properUnsignedNumberSerialization(boolean properUnsignedNumberSerialization) {
      this.properUnsignedNumberSerialization = properUnsignedNumberSerialization;
      return this;
    }

    public Builder serializeLongsAsString(boolean serializeLongsAsString) {
      this.serializeLongsAsString = serializeLongsAsString;
      return this;
    }

    public ProtobufJacksonConfig build() {
      return new ProtobufJacksonConfig(
        extensionRegistry,
        acceptLiteralFieldnames,
        properUnsignedNumberSerialization,
        serializeLongsAsString
      );
    }
  }
}
