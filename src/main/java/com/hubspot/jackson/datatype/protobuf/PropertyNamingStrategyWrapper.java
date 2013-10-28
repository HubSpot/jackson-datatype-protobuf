package com.hubspot.jackson.datatype.protobuf;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.PropertyNamingStrategyBase;
import com.google.common.base.CaseFormat;

public class PropertyNamingStrategyWrapper extends PropertyNamingStrategyBase {
  private static final PropertyNamingStrategyBase DEFAULT = new DefaultNamingStrategy();

  private final PropertyNamingStrategyBase delegate;

  public PropertyNamingStrategyWrapper(PropertyNamingStrategy delegate) {
    if (delegate instanceof PropertyNamingStrategyBase) {
      this.delegate = (PropertyNamingStrategyBase) delegate;
    } else {
      this.delegate = DEFAULT;
    }
  }

  @Override
  public String translate(String fieldName) {
    return delegate.translate(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, fieldName));
  }

  private static class DefaultNamingStrategy extends PropertyNamingStrategyBase {

    @Override
    public String translate(String fieldName) {
      return fieldName;
    }
  }
}
