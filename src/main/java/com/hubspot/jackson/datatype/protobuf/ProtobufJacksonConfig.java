package com.hubspot.jackson.datatype.protobuf;

import com.google.protobuf.ExtensionRegistry;

public class ProtobufJacksonConfig {
  private final ExtensionRegistryWrapper extensionRegistry;
  private final boolean acceptLiteralFieldnames;
  private final boolean serializeLongsAsStrings;

  private ProtobufJacksonConfig(ExtensionRegistryWrapper extensionRegistry, boolean acceptLiteralFieldnames, boolean serializeLongsAsStrings) {
    this.extensionRegistry = extensionRegistry;
    this.acceptLiteralFieldnames = acceptLiteralFieldnames;
    this.serializeLongsAsStrings = serializeLongsAsStrings;
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

  public boolean serializeLongsAsStrings() {
    return serializeLongsAsStrings;
  }

  public static class Builder {
    private ExtensionRegistryWrapper extensionRegistry = ExtensionRegistryWrapper.empty();
    private boolean acceptLiteralFieldnames = false;
    private boolean serializeLongsAsStrings = false;

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

    public Builder serializeLongsAsStrings(boolean serializeLongsAsStrings) {
      this.serializeLongsAsStrings = serializeLongsAsStrings;
      return this;
    }

    public ProtobufJacksonConfig build() {
      return new ProtobufJacksonConfig(extensionRegistry, acceptLiteralFieldnames, serializeLongsAsStrings);
    }
  }
}
