package com.hubspot.jackson.datatype.protobuf;

import com.google.common.base.CaseFormat;
import com.google.protobuf.Message;
import tools.jackson.databind.PropertyNamingStrategies.NamingBase;
import tools.jackson.databind.cfg.MapperConfig;

@SuppressWarnings("serial")
public class PropertyNamingStrategyWrapper {

  private static final NamingBase SNAKE_TO_CAMEL = new SnakeToCamelNamingStrategy();

  private final NamingBase delegate;

  public PropertyNamingStrategyWrapper(
    Class<? extends Message> messageType,
    MapperConfig<?> mapperConfig
  ) {
    if (mapperConfig.getPropertyNamingStrategy() instanceof NamingBase) {
      this.delegate = (NamingBase) mapperConfig.getPropertyNamingStrategy();
    } else {
      this.delegate = SNAKE_TO_CAMEL;
    }
  }

  public String translate(String fieldName) {
    // NOTE: the MapperConfig and AnnotatedField are not needed
    return delegate.nameForField(null, null, fieldName);
  }

  private static class SnakeToCamelNamingStrategy extends NamingBase {

    @Override
    public String translate(String fieldName) {
      return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, fieldName);
    }
  }
}
