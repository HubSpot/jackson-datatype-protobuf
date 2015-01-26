package com.hubspot.jackson.datatype.protobuf;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.PropertyNamingStrategyBase;
import com.google.common.base.CaseFormat;

@SuppressWarnings("serial")
public class PropertyNamingStrategyWrapper extends PropertyNamingStrategyBase {
  private static final PropertyNamingStrategyBase SNAKE_TO_CAMEL = new SnakeToCamelNamingStrategy();

  private final PropertyNamingStrategyBase delegate;

  public PropertyNamingStrategyWrapper(PropertyNamingStrategy delegate) {
    if (delegate instanceof PropertyNamingStrategyBase) {
      this.delegate = (PropertyNamingStrategyBase) delegate;
    } else {
      this.delegate = SNAKE_TO_CAMEL;
    }
  }

  @Override
  public String translate(String fieldName) {
    return delegate.translate(fieldName);
  }

  private static class SnakeToCamelNamingStrategy extends PropertyNamingStrategyBase {

    @Override
    public String translate(String fieldName) {
      return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, fieldName);
    }

  }
}
