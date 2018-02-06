package com.hubspot.jackson.datatype.protobuf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nonnull;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.type.SimpleType;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.protobuf.ByteString;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.EnumDescriptor;
import com.google.protobuf.Descriptors.EnumValueDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Message;
import com.google.protobuf.NullValue;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public abstract class ProtobufDeserializer<T extends Message, V extends Message.Builder> extends StdDeserializer<V> {
  private static final String NULL_VALUE_FULL_NAME = NullValue.getDescriptor().getFullName();
  private static final EnumValueDescriptor NULL_VALUE_DESCRIPTOR = NullValue.NULL_VALUE.getValueDescriptor();

  private final T defaultInstance;
  @SuppressFBWarnings(value="SE_BAD_FIELD")
  private final Map<FieldDescriptor, JsonDeserializer<Object>> deserializerCache;

  @SuppressWarnings("unchecked")
  public ProtobufDeserializer(Class<T> messageType) {
    super(messageType);

    try {
      this.defaultInstance = (T) messageType.getMethod("getDefaultInstance").invoke(null);
    } catch (Exception e) {
      throw new RuntimeException("Unable to get default instance for type " + messageType, e);
    }

    this.deserializerCache = new ConcurrentHashMap<>();
  }

  protected abstract void populate(
          V builder,
          JsonParser parser,
          DeserializationContext context
  ) throws IOException;

  public JsonDeserializer<T> buildAtEnd() {
    return new JsonDeserializer<T>() {

      @Override
      @SuppressWarnings("unchecked")
      public T deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        return (T) ProtobufDeserializer.this.deserialize(parser, context).build();
      }
    };
  }

  @Override
  @SuppressWarnings("unchecked")
  public V deserialize(JsonParser parser, DeserializationContext context) throws IOException {
    V builder = (V) defaultInstance.newBuilderForType();

    populate(builder, parser, context);

    return builder;
  }

  private void checkNullReturn(FieldDescriptor field, DeserializationContext context) throws JsonProcessingException {
    if (context.isEnabled(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)) {
      throw context.mappingException("Can not map JSON null into primitive field " + field.getFullName()
              + " (set DeserializationConfig.DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES to 'false' to allow)");
    }
  }

  protected List<Message> readMap(
          Message.Builder builder,
          FieldDescriptor field,
          JsonParser parser,
          DeserializationContext context
  ) throws IOException {
    if (parser.getCurrentToken() != JsonToken.START_OBJECT) {
      throw mappingException(field, context);
    }

    Descriptor entryDescriptor = field.getMessageType();
    FieldDescriptor keyDescriptor = entryDescriptor.findFieldByName("key");
    FieldDescriptor valueDescriptor = entryDescriptor.findFieldByName("value");

    List<Message> entries = new ArrayList<>();
    while (parser.nextToken() != JsonToken.END_OBJECT) {
      Message.Builder entryBuilder = builder.newBuilderForField(field);
      Object key = readValue(entryBuilder, keyDescriptor, null, parser, context);
      parser.nextToken(); // move from key to value
      Object value = readValue(entryBuilder, valueDescriptor, null, parser, context);

      entryBuilder.setField(keyDescriptor, key);
      entryBuilder.setField(valueDescriptor, value);
      entries.add(entryBuilder.build());
    }
    return entries;
  }

  protected Object readValue(
          Message.Builder builder,
          FieldDescriptor field,
          Message defaultInstance,
          JsonParser parser,
          DeserializationContext context
  ) throws IOException {
    if (parser.getCurrentToken() == JsonToken.START_ARRAY) {
      if (field.isRepeated()) {
        return readArray(builder, field, defaultInstance, parser, context);
      } else {
        throw mappingException(field, context);
      }
    }

    if (parser.getCurrentToken() == JsonToken.VALUE_NULL) {
      switch (field.getJavaType()) {
        case INT:
        case LONG:
        case FLOAT:
        case DOUBLE:
        case BOOLEAN:
          checkNullReturn(field, context);
          return null; // could just fall through, but findbugs is dumb
        default:
          return null;
      }
    }

    switch (field.getJavaType()) {
      case INT:
        return _parseIntPrimitive(parser, context);
      case LONG:
        return _parseLongPrimitive(parser, context);
      case FLOAT:
        return _parseFloatPrimitive(parser, context);
      case DOUBLE:
        return _parseDoublePrimitive(parser, context);
      case BOOLEAN:
        return _parseBooleanPrimitive(parser, context);
      case STRING:
        switch (parser.getCurrentToken()) {
          case VALUE_STRING:
            return parser.getText();
          default:
            return _parseString(parser, context);
        }
      case BYTE_STRING:
        switch (parser.getCurrentToken()) {
          case VALUE_STRING:
            return ByteString.copyFrom(context.getBase64Variant().decode(parser.getText()));
          default:
            throw mappingException(field, context);
        }
      case ENUM:
        final EnumValueDescriptor enumValueDescriptor;
        switch (parser.getCurrentToken()) {
          case VALUE_STRING:
            enumValueDescriptor = field.getEnumType().findValueByName(parser.getText());

            if (enumValueDescriptor == null && !ignorableEnum(parser.getText().trim(), context)) {
              throw context.weirdStringException(parser.getText(), field.getEnumType().getClass(),
                      "value not one of declared Enum instance names");
            }

            return enumValueDescriptor;
          case VALUE_NUMBER_INT:
            if (allowNumbersForEnums(context)) {
              enumValueDescriptor = field.getEnumType().findValueByNumber(parser.getIntValue());

              if (enumValueDescriptor == null && !ignoreUnknownEnums(context)) {
                throw context.weirdNumberException(parser.getIntValue(), field.getEnumType().getClass(),
                        "index value outside legal index range " + indexRange(field.getEnumType()));
              }

              return enumValueDescriptor;
            } else {
              throw context.mappingException("Not allowed to deserialize Enum value out of JSON number " +
                      "(disable DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS to allow)");
            }
          case VALUE_NULL:
            // special-case NullValue
            if (NULL_VALUE_FULL_NAME.equals(field.getEnumType().getFullName())) {
              return NULL_VALUE_DESCRIPTOR;
            }
          default:
            throw mappingException(field, context);
        }
      case MESSAGE:
        JsonDeserializer<Object> deserializer = deserializerCache.get(field);
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

        return deserializer.deserialize(parser, context);
      default:
        throw new IllegalArgumentException("Unrecognized field type: " + field.getJavaType());
    }
  }

  protected List<Object> readArray(
          Message.Builder builder,
          FieldDescriptor field,
          Message defaultInstance,
          JsonParser parser,
          DeserializationContext context
  ) throws IOException {
    if (parser.getCurrentToken() != JsonToken.START_ARRAY) {
      throw mappingException(field, context);
    }

    List<Object> values = Lists.newArrayList();
    while (parser.nextToken() != JsonToken.END_ARRAY) {
      Object value = readValue(builder, field, defaultInstance, parser, context);

      if (value != null) {
        values.add(value);
      }
    }
    return values;
  }

  private static boolean ignorableEnum(String value, DeserializationContext context) {
    return (acceptEmptyStringAsNull(context) && value.length() == 0) || ignoreUnknownEnums(context);
  }

  private static boolean acceptEmptyStringAsNull(DeserializationContext context) {
    return context.isEnabled(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
  }

  private static boolean allowNumbersForEnums(DeserializationContext context) {
    return !context.isEnabled(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS);
  }

  private static boolean ignoreUnknownEnums(DeserializationContext context) {
    return context.isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL);
  }

  private static String indexRange(EnumDescriptor field) {
    List<Integer> indices = Lists.transform(field.getValues(), new Function<EnumValueDescriptor, Integer>() {
      @Override
      public Integer apply(@Nonnull EnumValueDescriptor value) {
        return value.getIndex();
      }
    });

    // Guava returns non-modifiable list
    indices = Lists.newArrayList(indices);

    Collections.sort(indices);

    return "[" + Joiner.on(',').join(indices) + "]";
  }

  private static JsonMappingException mappingException(FieldDescriptor field, DeserializationContext context)
          throws IOException {
    JsonToken token = context.getParser().getCurrentToken();
    String message = "Can not deserialize instance of " + field.getJavaType() + " out of " + token + " token";
    throw context.mappingException(message);
  }
}
