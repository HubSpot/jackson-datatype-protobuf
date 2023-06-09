package com.hubspot.jackson.datatype.protobuf;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.google.protobuf.ExtensionRegistry;

public class ProtobufJacksonConfig {
  private final ExtensionRegistryWrapper extensionRegistry;
  private final boolean acceptLiteralFieldnames;
  private final LongWriter longWriter;

  private ProtobufJacksonConfig(ExtensionRegistryWrapper extensionRegistry, boolean acceptLiteralFieldnames, LongWriter longWriter) {
    this.extensionRegistry = extensionRegistry;
    this.acceptLiteralFieldnames = acceptLiteralFieldnames;
    this.longWriter = longWriter;
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

  public LongWriter longWriter() {
    return longWriter;
  }

  public static class Builder {
    private ExtensionRegistryWrapper extensionRegistry = ExtensionRegistryWrapper.empty();
    private boolean acceptLiteralFieldnames = false;
    private boolean writeLongsAsStrings = false;

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

    public Builder writeLongsAsStrings(boolean writeLongsAsStrings) {
      this.writeLongsAsStrings = writeLongsAsStrings;
      return this;
    }

    public ProtobufJacksonConfig build() {
      return new ProtobufJacksonConfig(extensionRegistry, acceptLiteralFieldnames, writeLongsAsStrings
          ? (generator, value) -> generator.writeString(String.valueOf(value)) : JsonGenerator::writeNumber);
    }
  }

  @FunctionalInterface
  public interface LongWriter {
    void write(JsonGenerator generator, long value) throws IOException;
  }
}
