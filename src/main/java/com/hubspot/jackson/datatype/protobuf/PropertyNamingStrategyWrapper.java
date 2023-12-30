package com.hubspot.jackson.datatype.protobuf;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.NamingBase;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.AnnotationMap;
import com.fasterxml.jackson.databind.introspect.TypeResolutionContext;
import com.google.common.base.CaseFormat;
import com.google.protobuf.Message;
import java.lang.reflect.Field;

@SuppressWarnings("serial")
public class PropertyNamingStrategyWrapper {

  private static final PropertyNamingStrategy SNAKE_TO_CAMEL = new SnakeToCamelNamingStrategy();
  private static final PropertyNamingStrategy NO_OP = new NoOpNamingStrategy();

  private final Class<?> messageType;
  private final MapperConfig<?> mapperConfig;
  private final PropertyNamingStrategy delegate;

  public PropertyNamingStrategyWrapper(
    Class<? extends Message> messageType,
    MapperConfig<?> mapperConfig
  ) {
    this.messageType = messageType;
    this.mapperConfig = mapperConfig;

    if (mapperConfig.getPropertyNamingStrategy() == null) {
      this.delegate = SNAKE_TO_CAMEL;
    } else if (
      mapperConfig.getPropertyNamingStrategy() ==
      PropertyNamingStrategies.LOWER_CAMEL_CASE
    ) {
      this.delegate = NO_OP;
    } else {
      this.delegate = mapperConfig.getPropertyNamingStrategy();
    }
  }

  public String translate(String fieldName) {
    AnnotatedField annotatedField = null;
    try {
      Field field = messageType.getDeclaredField(fieldName + "_");
      annotatedField =
        new AnnotatedField(
          new TypeResolutionContext.Empty(mapperConfig.getTypeFactory()),
          field,
          new AnnotationMap()
        );
    } catch (ReflectiveOperationException e) {
      // ignored
    }

    return delegate.nameForField(mapperConfig, annotatedField, fieldName);
  }

  private static class SnakeToCamelNamingStrategy extends NamingBase {

    @Override
    public String translate(String fieldName) {
      return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, fieldName);
    }
  }

  private static class NoOpNamingStrategy extends NamingBase {

    @Override
    public String translate(String fieldName) {
      return fieldName;
    }
  }
}
