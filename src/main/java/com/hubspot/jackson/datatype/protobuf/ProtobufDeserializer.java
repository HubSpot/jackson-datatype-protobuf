package com.hubspot.jackson.datatype.protobuf;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.PropertyNamingStrategyBase;
import com.fasterxml.jackson.databind.deser.impl.NullProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.type.SimpleType;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.protobuf.ByteString;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.EnumDescriptor;
import com.google.protobuf.Descriptors.EnumValueDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Message;
import com.google.protobuf.MessageOrBuilder;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nonnull;

public class ProtobufDeserializer<T extends Message> extends StdDeserializer<MessageOrBuilder> {
  private final T defaultInstance;
  private final boolean build;
  @SuppressFBWarnings(value="SE_BAD_FIELD")
  private final Map<FieldDescriptor, JsonDeserializer<Object>> deserializerCache;

  @SuppressWarnings("unchecked")
  public ProtobufDeserializer(Class<T> messageType, boolean build) throws JsonMappingException {
    super(messageType);

    try {
      this.defaultInstance = (T) messageType.getMethod("getDefaultInstance").invoke(null);
    } catch (Exception e) {
      throw new JsonMappingException("Unable to get default instance for type " + messageType, e);
    }

    this.build = build;
    this.deserializerCache = new ConcurrentHashMap<>();
  }

  @Override
  public MessageOrBuilder deserialize(JsonParser parser, DeserializationContext context) throws IOException {
    Message.Builder builder = defaultInstance.newBuilderForType();

    populate(builder, parser, context);

    if (build) {
      return builder.build();
    } else {
      return builder;
    }
  }

  private void populate(Message.Builder builder, JsonParser parser, DeserializationContext context)
          throws IOException {
    JsonToken token = parser.getCurrentToken();
    if (token == JsonToken.START_ARRAY) {
      token = parser.nextToken();
    }

    switch (token) {
      case END_OBJECT:
        return;
      case START_OBJECT:
        token = parser.nextToken();
        break;
      default:
        break; // make findbugs happy
    }

    Descriptor descriptor = builder.getDescriptorForType();
    Map<String, FieldDescriptor> fieldLookup = buildFieldLookup(descriptor, context);

    do {
      if (!token.equals(JsonToken.FIELD_NAME)) {
        throw context.wrongTokenException(parser, JsonToken.FIELD_NAME, "");
      }

      FieldDescriptor field = fieldLookup.get(parser.getCurrentName());
      if (field == null) {
        if (!context.handleUnknownProperty(parser, this, builder, parser.getCurrentName())) {
          context.reportUnknownProperty(builder, parser.getCurrentName(), this);
        }

        parser.nextToken();
        parser.skipChildren();
        continue;
      }

      parser.nextToken();
      setField(builder, field, parser, context);
    } while ((token = parser.nextToken()) != JsonToken.END_OBJECT);
  }

  private Map<String, FieldDescriptor> buildFieldLookup(Descriptor descriptor, DeserializationContext context) {
    PropertyNamingStrategyBase namingStrategy =
            new PropertyNamingStrategyWrapper(context.getConfig().getPropertyNamingStrategy());

    Map<String, FieldDescriptor> fieldLookup = Maps.newHashMap();

    for (FieldDescriptor field : descriptor.getFields()) {
      fieldLookup.put(namingStrategy.translate(field.getName()), field);
    }

    return fieldLookup;
  }

  private void setField(Message.Builder builder, FieldDescriptor field, JsonParser parser,
                        DeserializationContext context) throws IOException {
    Object value = readValue(builder, field, parser, context);

    if (value != null) {
      if (field.isRepeated()) {
        if (value instanceof Iterable) {
          for (Object subValue : (Iterable<?>) value) {
            builder.addRepeatedField(field, subValue);
          }
        } else if (context.isEnabled(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)) {
          builder.addRepeatedField(field, value);
        } else {
          throw mappingException(field, context);
        }
      } else {
        builder.setField(field, value);
      }
    }
  }

  private Object readValue(Message.Builder builder, FieldDescriptor field, JsonParser parser,
                           DeserializationContext context) throws IOException {
    final Object value;

    if (parser.getCurrentToken() == JsonToken.START_ARRAY) {
      if (field.isRepeated()) {
        return readArray(builder, field, parser, context);
      } else {
        throw mappingException(field, context);
      }
    }

    switch (field.getJavaType()) {
      case INT:
        value = _parseInteger(parser, context);

        if (value == null) {
          new NullProvider(SimpleType.construct(Integer.TYPE), 0).nullValue(context);
        }
        break;
      case LONG:
        value = _parseLong(parser, context);

        if (value == null) {
          new NullProvider(SimpleType.construct(Long.TYPE), 0L).nullValue(context);
        }
        break;
      case FLOAT:
        value = _parseFloat(parser, context);

        if (value == null) {
          new NullProvider(SimpleType.construct(Float.TYPE), 0.0f).nullValue(context);
        }
        break;
      case DOUBLE:
        value = _parseDouble(parser, context);

        if (value == null) {
          new NullProvider(SimpleType.construct(Double.TYPE), 0.0d).nullValue(context);
        }
        break;
      case BOOLEAN:
        value = _parseBoolean(parser, context);

        if (value == null) {
          new NullProvider(SimpleType.construct(Boolean.TYPE), false).nullValue(context);
        }
        break;
      case STRING:
        switch (parser.getCurrentToken()) {
          case VALUE_STRING:
            value = parser.getText();
            break;
          case VALUE_NULL:
            value = null;
            break;
          default:
            value = _parseString(parser, context);
        }
        break;
      case BYTE_STRING:
        switch (parser.getCurrentToken()) {
          case VALUE_STRING:
            value = ByteString.copyFrom(context.getBase64Variant().decode(parser.getText()));
            break;
          case VALUE_NULL:
            value = null;
            break;
          default:
            throw mappingException(field, context);
        }
        break;
      case ENUM:
        switch (parser.getCurrentToken()) {
          case VALUE_STRING:
            value = field.getEnumType().findValueByName(parser.getText());

            if (value == null && !ignorableEnum(parser.getText().trim(), context)) {
              throw context.weirdStringException(parser.getText(), field.getEnumType().getClass(),
                      "value not one of declared Enum instance names");
            }
            break;
          case VALUE_NUMBER_INT:
            if (allowNumbersForEnums(context)) {
              value = field.getEnumType().findValueByNumber(parser.getIntValue());

              if (value == null && !ignoreUnknownEnums(context)) {
                throw context.weirdNumberException(parser.getIntValue(), field.getEnumType().getClass(),
                        "index value outside legal index range " + indexRange(field.getEnumType()));
              }
            } else {
              throw context.mappingException("Not allowed to deserialize Enum value out of JSON number " +
                      "(disable DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS to allow)");
            }
            break;
          case VALUE_NULL:
            value = null;
            break;
          default:
            throw mappingException(field, context);
        }
        break;
      case MESSAGE:
        switch (parser.getCurrentToken()) {
          case START_OBJECT:
            final JsonDeserializer<Object> deserializer;
            if (deserializerCache.containsKey(field)) {
              deserializer = deserializerCache.get(field);
            } else {
              Message.Builder subBuilder = builder.newBuilderForField(field);
              Class<?> subType = subBuilder.getDefaultInstanceForType().getClass();

              JavaType type = SimpleType.construct(subType);
              deserializer = context.findContextualValueDeserializer(type, null);
              deserializerCache.put(field, deserializer);
            }

            value = deserializer.deserialize(parser, context);
            break;
          case VALUE_NULL:
            value = null;
            break;
          default:
            throw mappingException(field, context);
        }
        break;
      default:
        throw mappingException(field, context);
    }

    return value;
  }

  private List<Object> readArray(Message.Builder builder, FieldDescriptor field, JsonParser parser,
                                 DeserializationContext context) throws IOException {
    List<Object> values = Lists.newArrayList();
    while (parser.nextToken() != JsonToken.END_ARRAY) {
      Object value = readValue(builder, field, parser, context);

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
