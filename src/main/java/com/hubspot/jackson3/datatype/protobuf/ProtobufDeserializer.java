package com.hubspot.jackson3.datatype.protobuf;

import com.google.common.collect.Lists;
import com.google.protobuf.ByteString;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.EnumDescriptor;
import com.google.protobuf.Descriptors.EnumValueDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Message;
import com.google.protobuf.NullValue;
import com.hubspot.jackson3.datatype.protobuf.builtin.deserializers.MessageDeserializer;
import com.hubspot.jackson3.datatype.protobuf.internal.Constants;
import com.hubspot.jackson3.datatype.protobuf.internal.Types;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.exc.InputCoercionException;
import tools.jackson.core.io.NumberInput;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.cfg.EnumFeature;
import tools.jackson.databind.deser.jdk.NumberDeserializers;
import tools.jackson.databind.deser.std.StdDeserializer;

public abstract class ProtobufDeserializer<T extends Message, V extends Message.Builder>
  extends StdDeserializer<V> {

  private static final String NULL_VALUE_FULL_NAME = NullValue
    .getDescriptor()
    .getFullName();
  private static final EnumValueDescriptor NULL_VALUE_DESCRIPTOR =
    NullValue.NULL_VALUE.getValueDescriptor();

  private final T defaultInstance;

  @SuppressFBWarnings(value = "SE_BAD_FIELD")
  private final Map<FieldDescriptor, ValueDeserializer<Object>> deserializerCache;

  @SuppressWarnings("unchecked")
  public ProtobufDeserializer(Class<T> messageType) {
    super(messageType);
    try {
      this.defaultInstance = (T) messageType.getMethod("getDefaultInstance").invoke(null);
    } catch (Exception e) {
      throw new RuntimeException(
        "Unable to get default instance for type " + messageType,
        e
      );
    }

    this.deserializerCache = new ConcurrentHashMap<>();
  }

  protected Descriptor getDescriptor() {
    return defaultInstance.getDescriptorForType();
  }

  protected abstract void populate(
    V builder,
    JsonParser parser,
    DeserializationContext context
  );

  public ValueDeserializer<T> buildAtEnd() {
    @SuppressWarnings("unchecked")
    Class<T> messageType = (Class<T>) handledType();
    return new BuildingDeserializer<T, V>(messageType) {
      @Override
      public ValueDeserializer<V> getWrappedDeserializer() {
        return ProtobufDeserializer.this;
      }
    };
  }

  @Override
  @SuppressWarnings("unchecked")
  public V deserialize(JsonParser parser, DeserializationContext context) {
    V builder = (V) defaultInstance.newBuilderForType();

    populate(builder, parser, context);

    return builder;
  }

  private void checkNullReturn(FieldDescriptor field, DeserializationContext context) {
    if (context.isEnabled(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)) {
      throw reportInputMismatch(
        context,
        "Can not map JSON null into primitive field " +
        field.getFullName() +
        " (set DeserializationConfig.DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES to 'false' to allow)"
      );
    }
  }

  protected List<Message> readMap(
    Message.Builder builder,
    FieldDescriptor field,
    JsonParser parser,
    DeserializationContext context
  ) {
    if (parser.currentToken() == JsonToken.VALUE_NULL) {
      // Seems like we should treat null as an empty map rather than fail?
      return Collections.emptyList();
    } else if (parser.currentToken() != JsonToken.START_OBJECT) {
      throw reportWrongToken(
        JsonToken.START_OBJECT,
        context,
        "Can't parse map field out of " + parser.currentToken() + " token"
      );
    }

    Descriptor entryDescriptor = field.getMessageType();
    FieldDescriptor keyDescriptor = entryDescriptor.findFieldByName("key");
    FieldDescriptor valueDescriptor = entryDescriptor.findFieldByName("value");

    List<Message> entries = new ArrayList<>();
    while (parser.nextToken() != JsonToken.END_OBJECT) {
      Message.Builder entryBuilder = builder.newBuilderForField(field);
      Object key = readKey(keyDescriptor, parser, context);
      parser.nextToken(); // move from key to value
      Object value = readValue(entryBuilder, valueDescriptor, null, parser, context);

      entryBuilder.setField(keyDescriptor, key);
      entryBuilder.setField(valueDescriptor, value);
      entries.add(entryBuilder.build());
    }
    return entries;
  }

  /**
   * Specialized version of readValue just for reading map keys, because the StdDeserializer methods like
   * _parseIntPrimitive blow up when the current JsonToken is FIELD_NAME
   */
  private Object readKey(
    FieldDescriptor field,
    JsonParser parser,
    DeserializationContext context
  ) {
    if (parser.currentToken() != JsonToken.PROPERTY_NAME) {
      throw reportWrongToken(
        JsonToken.PROPERTY_NAME,
        context,
        "Expected PROPERTY_NAME token"
      );
    }

    String fieldName = parser.currentName();
    switch (field.getJavaType()) {
      case INT:
        // lifted from StdDeserializer since there's no method to call
        try {
          return NumberInput.parseInt(fieldName.trim());
        } catch (IllegalArgumentException iae) {
          Number number = (Number) context.handleWeirdStringValue(
            _valueClass,
            fieldName.trim(),
            "not a valid int value"
          );
          return number == null ? 0 : number.intValue();
        }
      case LONG:
        // lifted from StdDeserializer since there's no method to call
        try {
          return NumberInput.parseLong(fieldName.trim());
        } catch (IllegalArgumentException iae) {
          Number number = (Number) context.handleWeirdStringValue(
            _valueClass,
            fieldName.trim(),
            "not a valid long value"
          );
          return number == null ? 0L : number.longValue();
        }
      case BOOLEAN:
        // lifted from StdDeserializer since there's no method to call
        String text = fieldName.trim();
        if ("true".equals(text) || "True".equals(text)) {
          return true;
        }
        if ("false".equals(text) || "False".equals(text)) {
          return false;
        }
        Boolean b = (Boolean) context.handleWeirdStringValue(
          _valueClass,
          text,
          "only \"true\" or \"false\" recognized"
        );
        return Boolean.TRUE.equals(b);
      case STRING:
        return fieldName;
      case ENUM:
        EnumValueDescriptor enumValueDescriptor = field
          .getEnumType()
          .findValueByName(parser.getString());

        if (
          enumValueDescriptor == null &&
          !ignorableEnum(parser.getString().trim(), context)
        ) {
          throw context.weirdStringException(
            parser.getString(),
            field.getEnumType().getClass(),
            "value not one of declared Enum instance names"
          );
        }

        return enumValueDescriptor;
      default:
        throw new IllegalArgumentException(
          "Unexpected map key type: " + field.getJavaType()
        );
    }
  }

  protected Object readValue(
    Message.Builder builder,
    FieldDescriptor field,
    Message defaultInstance,
    JsonParser parser,
    DeserializationContext context
  ) {
    if (parser.currentToken() == JsonToken.START_ARRAY) {
      if (field.getJavaType() == FieldDescriptor.JavaType.MESSAGE) {
        // might have a custom serializer/deserializer registered
        ValueDeserializer<Object> deserializer = getMessageDeserializer(
          builder,
          field,
          defaultInstance,
          context
        );
        if (!isDefaultMessageDeserializer(deserializer)) {
          return deserializer.deserialize(parser, context);
        }
      }

      throw reportInputMismatch(
        context,
        "Encountered START_ARRAY token for non-repeated field " + field.getFullName()
      );
    }

    if (parser.currentToken() == JsonToken.VALUE_NULL) {
      switch (field.getJavaType()) {
        case INT:
        case LONG:
        case FLOAT:
        case DOUBLE:
        case BOOLEAN:
          checkNullReturn(field, context);
          return null;
        case ENUM:
          if (NULL_VALUE_FULL_NAME.equals(field.getEnumType().getFullName())) {
            return NULL_VALUE_DESCRIPTOR;
          } else {
            return null;
          }
        case MESSAGE:
          // don't return null yet, might have a custom serializer/deserializer registered
          ValueDeserializer<Object> deserializer = getMessageDeserializer(
            builder,
            field,
            defaultInstance,
            context
          );
          return deserializer.getNullValue(context);
        default:
          return null;
      }
    }

    switch (field.getJavaType()) {
      case INT:
        return parseInt(field, parser, context);
      case LONG:
        return parseLong(field, parser, context);
      case FLOAT:
        return _parseFloatPrimitive(parser, context);
      case DOUBLE:
        return _parseDoublePrimitive(parser, context);
      case BOOLEAN:
        return _parseBooleanPrimitive(parser, context);
      case STRING:
        switch (parser.currentToken()) {
          case VALUE_STRING:
            return parser.getString();
          default:
            return _parseString(parser, context, this);
        }
      case BYTE_STRING:
        switch (parser.currentToken()) {
          case VALUE_STRING:
            return ByteString.copyFrom(
              context.getBase64Variant().decode(parser.getString())
            );
          default:
            throw reportWrongToken(field, JsonToken.VALUE_STRING, context);
        }
      case ENUM:
        final EnumValueDescriptor enumValueDescriptor;
        switch (parser.currentToken()) {
          case VALUE_STRING:
            enumValueDescriptor = field.getEnumType().findValueByName(parser.getString());

            if (
              enumValueDescriptor == null &&
              !ignorableEnum(parser.getString().trim(), context)
            ) {
              throw context.weirdStringException(
                parser.getString(),
                field.getEnumType().getClass(),
                "value not one of declared Enum instance names"
              );
            }

            return enumValueDescriptor;
          case VALUE_NUMBER_INT:
            if (allowNumbersForEnums(context)) {
              enumValueDescriptor =
                field.getEnumType().findValueByNumber(parser.getIntValue());

              if (enumValueDescriptor == null && !ignoreUnknownEnums(context)) {
                throw context.weirdNumberException(
                  parser.getIntValue(),
                  field.getEnumType().getClass(),
                  "index value outside legal index range " +
                  indexRange(field.getEnumType())
                );
              }

              return enumValueDescriptor;
            } else {
              throw reportWrongToken(
                JsonToken.VALUE_STRING,
                context,
                "Not allowed to deserialize Enum " +
                "value out of JSON number (disable DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS to allow)"
              );
            }
          default:
            throw reportWrongToken(field, JsonToken.VALUE_STRING, context);
        }
      case MESSAGE:
        ValueDeserializer<Object> deserializer = getMessageDeserializer(
          builder,
          field,
          defaultInstance,
          context
        );
        return deserializer.deserialize(parser, context);
      default:
        throw new IllegalArgumentException(
          "Unrecognized field type: " + field.getJavaType()
        );
    }
  }

  protected List<Object> readArray(
    Message.Builder builder,
    FieldDescriptor field,
    Message defaultInstance,
    JsonParser parser,
    DeserializationContext context
  ) {
    switch (parser.currentToken()) {
      case START_ARRAY:
        List<Object> values = Lists.newArrayList();
        while (parser.nextToken() != JsonToken.END_ARRAY) {
          Object value = readValue(builder, field, defaultInstance, parser, context);

          if (value != null) {
            values.add(value);
          }
        }
        return values;
      case VALUE_NULL:
        // Seems like we should treat null as an empty list rather than fail?
        return Collections.emptyList();
      default:
        if (context.isEnabled(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)) {
          Object value = readValue(builder, field, defaultInstance, parser, context);
          return Collections.singletonList(value);
        } else {
          throw reportInputMismatch(
            context,
            "Expected JSON array for repeated field " + field.getFullName()
          );
        }
    }
  }

  private int parseInt(
    FieldDescriptor field,
    JsonParser parser,
    DeserializationContext context
  ) {
    if (Types.isUnsigned(field.getType())) {
      long longValue = _parseLongPrimitive(parser, context);
      if (longValue < Constants.MIN_UINT32 || longValue > Constants.MAX_UINT32) {
        throw new InputCoercionException(
          parser,
          "Value " + longValue + " is out of range for " + field.getType(),
          parser.currentToken(),
          Integer.TYPE
        );
      } else {
        return (int) longValue;
      }
    } else {
      return _parseIntPrimitive(parser, context);
    }
  }

  private long parseLong(
    FieldDescriptor field,
    JsonParser parser,
    DeserializationContext context
  ) {
    if (Types.isUnsigned(field.getType())) {
      BigInteger bigIntegerValue =
        NumberDeserializers.BigIntegerDeserializer.instance.deserialize(parser, context);
      if (
        bigIntegerValue.compareTo(Constants.MIN_UINT64) < 0 ||
        bigIntegerValue.compareTo(Constants.MAX_UINT64) > 0
      ) {
        throw new InputCoercionException(
          parser,
          "Value " + bigIntegerValue + " is out of range for " + field.getType(),
          parser.currentToken(),
          Long.TYPE
        );
      } else {
        return bigIntegerValue.longValue();
      }
    } else {
      return _parseLongPrimitive(parser, context);
    }
  }

  private ValueDeserializer<Object> getMessageDeserializer(
    Message.Builder builder,
    FieldDescriptor field,
    Message defaultInstance,
    DeserializationContext context
  ) {
    ValueDeserializer<Object> deserializer = deserializerCache.get(field);
    if (deserializer == null) {
      final Class<?> subType;
      if (defaultInstance == null) {
        Message.Builder subBuilder = builder.newBuilderForField(field);
        subType = subBuilder.getDefaultInstanceForType().getClass();
      } else {
        subType = defaultInstance.getClass();
      }

      JavaType type = context.constructType(subType);
      deserializer = context.findContextualValueDeserializer(type, null);
      deserializerCache.put(field, deserializer);
    }

    return deserializer;
  }

  private AssertionError reportInputMismatch(
    DeserializationContext context,
    String message
  ) {
    context.reportInputMismatch(this, message);
    // the previous method should have thrown
    throw new AssertionError();
  }

  private AssertionError reportWrongToken(
    FieldDescriptor field,
    JsonToken expected,
    DeserializationContext context
  ) {
    return reportWrongToken(expected, context, wrongTokenMessage(field, context));
  }

  private AssertionError reportWrongToken(
    JsonToken expected,
    DeserializationContext context,
    String message
  ) {
    context.reportWrongTokenException(this, expected, message);
    // the previous method should have thrown
    throw new AssertionError();
  }

  private static boolean isDefaultMessageDeserializer(ValueDeserializer<?> deserializer) {
    final Class<?> deserializerType;
    if (deserializer instanceof BuildingDeserializer) {
      deserializerType =
        ((BuildingDeserializer) deserializer).getWrappedDeserializer().getClass();
    } else {
      deserializerType = deserializer.getClass();
    }
    return MessageDeserializer.class.equals(deserializerType);
  }

  private static boolean ignorableEnum(String value, DeserializationContext context) {
    return (
      (acceptEmptyStringAsNull(context) && value.isEmpty()) || ignoreUnknownEnums(context)
    );
  }

  private static boolean acceptEmptyStringAsNull(DeserializationContext context) {
    return context.isEnabled(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
  }

  private static boolean allowNumbersForEnums(DeserializationContext context) {
    return !context.isEnabled(EnumFeature.FAIL_ON_NUMBERS_FOR_ENUMS);
  }

  private static boolean ignoreUnknownEnums(DeserializationContext context) {
    return context.isEnabled(EnumFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL);
  }

  private static String indexRange(EnumDescriptor field) {
    return field
      .getValues()
      .stream()
      .map(EnumValueDescriptor::getIndex)
      .sorted()
      .map(String::valueOf)
      .collect(Collectors.joining(",", "[", "]"));
  }

  private static String wrongTokenMessage(
    FieldDescriptor field,
    DeserializationContext context
  ) {
    return (
      "Can not deserialize instance of " +
      field.getJavaType() +
      " out of " +
      context.getParser().currentToken() +
      " token"
    );
  }
}
