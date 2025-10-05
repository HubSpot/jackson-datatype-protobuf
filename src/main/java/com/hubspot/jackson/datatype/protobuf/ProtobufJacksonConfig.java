package com.hubspot.jackson.datatype.protobuf;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.NamingBase;
import com.google.protobuf.ExtensionRegistry;
import com.hubspot.jackson.datatype.protobuf.internal.PropertyNamingCache;

public class ProtobufJacksonConfig {

  private static final ProtobufJacksonConfig DEFAULT_INSTANCE = ProtobufJacksonConfig
    .builder()
    .build();

  public static class PropertyNamingStrategies {

    public static final NamingBase SNAKE_TO_CAMEL =
      PropertyNamingStrategyWrapper.SNAKE_TO_CAMEL;
    public static final NamingBase JSON_FORMAT =
      PropertyNamingCache.JsonFormatPropertyNamingStrategy.INSTANCE;
  }

  private final ExtensionRegistryWrapper extensionRegistry;
  private final boolean acceptLiteralFieldnames;
  private final boolean properUnsignedNumberSerialization;
  private final boolean serializeLongsAsString;
  private final NamingBase propertyNamingStrategy;

  private ProtobufJacksonConfig(
    ExtensionRegistryWrapper extensionRegistry,
    boolean acceptLiteralFieldnames,
    boolean properUnsignedNumberSerialization,
    boolean serializeLongsAsString,
    NamingBase propertyNamingStrategy
  ) {
    this.extensionRegistry = extensionRegistry;
    this.acceptLiteralFieldnames = acceptLiteralFieldnames;
    this.properUnsignedNumberSerialization = properUnsignedNumberSerialization;
    this.serializeLongsAsString = serializeLongsAsString;
    this.propertyNamingStrategy = propertyNamingStrategy;
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

  public NamingBase propertyNamingStrategy() {
    return propertyNamingStrategy;
  }

  public static class Builder {

    private ExtensionRegistryWrapper extensionRegistry = ExtensionRegistryWrapper.empty();
    private boolean acceptLiteralFieldnames = false;
    private boolean properUnsignedNumberSerialization = false;
    private boolean serializeLongsAsString = false;
    private NamingBase propertyNamingStrategy = PropertyNamingStrategies.SNAKE_TO_CAMEL;

    private Builder() {}

    public Builder extensionRegistry(ExtensionRegistry extensionRegistry) {
      return extensionRegistry(ExtensionRegistryWrapper.wrap(extensionRegistry));
    }

    public Builder extensionRegistry(ExtensionRegistryWrapper extensionRegistry) {
      this.extensionRegistry = extensionRegistry;
      return this;
    }

    public Builder useCanonicalSerialization() {
      acceptLiteralFieldnames(true);
      properUnsignedNumberSerialization(true);
      serializeLongsAsString(true);
      propertyNamingStrategy(PropertyNamingStrategies.JSON_FORMAT);
      return this;
    }

    public Builder acceptLiteralFieldnames(boolean acceptLiteralFieldnames) {
      this.acceptLiteralFieldnames = acceptLiteralFieldnames;
      return this;
    }

    public Builder properUnsignedNumberSerialization(
      boolean properUnsignedNumberSerialization
    ) {
      this.properUnsignedNumberSerialization = properUnsignedNumberSerialization;
      return this;
    }

    public Builder serializeLongsAsString(boolean serializeLongsAsString) {
      this.serializeLongsAsString = serializeLongsAsString;
      return this;
    }

    public Builder propertyNamingStrategy(NamingBase propertyNamingStrategy) {
      this.propertyNamingStrategy =
        propertyNamingStrategy == null
          ? PropertyNamingStrategies.SNAKE_TO_CAMEL
          : propertyNamingStrategy;
      return this;
    }

    public ProtobufJacksonConfig build() {
      return new ProtobufJacksonConfig(
        extensionRegistry,
        acceptLiteralFieldnames,
        properUnsignedNumberSerialization,
        serializeLongsAsString,
        propertyNamingStrategy
      );
    }
  }
}
