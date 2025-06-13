package com.hubspot.jackson.datatype.protobuf;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.NamingBase;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.google.common.base.CaseFormat;
import com.google.protobuf.Message;

@SuppressWarnings("serial")
public class PropertyNamingStrategyWrapper {

  static final NamingBase SNAKE_TO_CAMEL = new SnakeToCamelNamingStrategy();

  private final NamingBase delegate;

  public PropertyNamingStrategyWrapper(
    Class<? extends Message> messageType,
    MapperConfig<?> mapperConfig,
    ProtobufJacksonConfig protobufJacksonConfig
  ) {
    if (mapperConfig.getPropertyNamingStrategy() instanceof NamingBase) {
      this.delegate = (NamingBase) mapperConfig.getPropertyNamingStrategy();
    } else {
      this.delegate = protobufJacksonConfig.propertyNamingStrategy();
    }
  }

  public String translate(String fieldName) {
    return delegate.translate(fieldName);
  }

  private static class SnakeToCamelNamingStrategy extends NamingBase {

    @Override
    public String translate(String fieldName) {
      return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, fieldName);
    }
  }
}
