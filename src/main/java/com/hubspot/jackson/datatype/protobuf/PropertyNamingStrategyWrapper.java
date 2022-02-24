package com.hubspot.jackson.datatype.protobuf;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.PropertyNamingStrategyBase;
import com.google.common.base.CaseFormat;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

@SuppressWarnings("deprecation")
public class PropertyNamingStrategyWrapper extends PropertyNamingStrategyBase {
  private static final PropertyNamingStrategyBase SNAKE_TO_CAMEL = new SnakeToCamelNamingStrategy();
  private static final PropertyNamingStrategyBase NO_OP = new NoOpNamingStrategy();

  private final PropertyNamingStrategyBase delegate;

  public PropertyNamingStrategyWrapper(PropertyNamingStrategy delegate) {
    if (delegate instanceof PropertyNamingStrategyBase) {
      this.delegate = (PropertyNamingStrategyBase) delegate;
    } else if (NamingBaseAdapter.extendsNamingBase(delegate)) {
      this.delegate = new NamingBaseAdapter(delegate);
    } else if (delegate == PropertyNamingStrategy.LOWER_CAMEL_CASE) {
      this.delegate = NO_OP;
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

  private static class NoOpNamingStrategy extends PropertyNamingStrategyBase {

    @Override
    public String translate(String fieldName) {
      return fieldName;
    }
  }

  private static class NamingBaseAdapter extends PropertyNamingStrategyBase {
    private static final Class<?> NAMING_BASE = tryToLoadNamingBase();
    private static final MethodHandle TRANSLATE_METHOD = tryToLoadTranslateMethod(NAMING_BASE);

    private final PropertyNamingStrategy delegate;

    private NamingBaseAdapter(PropertyNamingStrategy delegate) {
      this.delegate = delegate;
    }

    public static boolean extendsNamingBase(PropertyNamingStrategy namingStrategy) {
      return NAMING_BASE != null && NAMING_BASE.isInstance(namingStrategy);
    }

    @Override
    public String translate(String fieldName) {
      try {
        return (String) TRANSLATE_METHOD.invokeExact(delegate, fieldName);
      } catch (Throwable e) {
        throw new RuntimeException("Unable to invoke translate method", e);
      }
    }

    private static Class<?> tryToLoadNamingBase() {
      try {
        return Class.forName("com.fasterxml.jackson.databind.PropertyNamingStrategies$NamingBase");
      } catch (ClassNotFoundException e) {
        return null;
      }
    }

    private static MethodHandle tryToLoadTranslateMethod(Class<?> namingBase) {
      if (namingBase == null) {
        return null;
      } else {
        try {
          return MethodHandles.publicLookup().findVirtual(namingBase, "translate", MethodType.methodType(String.class, String.class));
        } catch (NoSuchMethodException | IllegalAccessException e) {
          throw new RuntimeException("Unable to find translate method on class: " + namingBase);
        }
      }
    }
  }
}
