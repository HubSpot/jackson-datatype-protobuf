package com.hubspot.jackson3.datatype.protobuf;

import com.google.common.base.CaseFormat;
import com.google.protobuf.Message;
import tools.jackson.databind.MakeTranslateMethodPublicHack;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.cfg.MapperConfig;

@SuppressWarnings("serial")
public class PropertyNamingStrategyWrapper {

  private static final PropertyNamingStrategies.NamingBase SNAKE_TO_CAMEL =
    new SnakeToCamelNamingStrategy();

  private final PropertyNamingStrategies.NamingBase delegate;

  public PropertyNamingStrategyWrapper(
    Class<? extends Message> messageType,
    MapperConfig<?> mapperConfig
  ) {
    if (
      mapperConfig.getPropertyNamingStrategy() instanceof
      PropertyNamingStrategies.NamingBase
    ) {
      this.delegate =
        (PropertyNamingStrategies.NamingBase) mapperConfig.getPropertyNamingStrategy();
    } else {
      this.delegate = SNAKE_TO_CAMEL;
    }
  }

  public String translate(String fieldName) {
    return MakeTranslateMethodPublicHack.translate(delegate, fieldName);
  }

  private static class SnakeToCamelNamingStrategy
    extends PropertyNamingStrategies.NamingBase {

    @Override
    protected String translate(String fieldName) {
      return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, fieldName);
    }
  }
}
