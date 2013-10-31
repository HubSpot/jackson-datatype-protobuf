package com.hubspot.jackson.datatype.protobuf;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
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

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ProtobufDeserializer<T extends Message> extends StdDeserializer<MessageOrBuilder> {
  private final T defaultInstance;
  private final boolean build;

  @SuppressWarnings("unchecked")
  public ProtobufDeserializer(Class<T> messageType, boolean build) throws JsonMappingException {
    super(messageType);

    try {
      this.defaultInstance = (T) messageType.getMethod("getDefaultInstance").invoke(null);
    } catch (Exception e) {
      throw new JsonMappingException("Unable to get default instance for type " + messageType, e);
    }

    this.build = build;
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
    if (!JsonToken.START_OBJECT.equals(parser.getCurrentToken()) &&
        !JsonToken.START_OBJECT.equals(parser.nextToken())) {
      throw new JsonParseException("Expected start object token", parser.getCurrentLocation());
    }

    Descriptor descriptor = builder.getDescriptorForType();
    Map<String, FieldDescriptor> fieldLookup = buildFieldLookup(descriptor, context);

    while (!parser.nextToken().equals(JsonToken.END_OBJECT)) {
      JsonToken token = parser.getCurrentToken();
      if (!token.equals(JsonToken.FIELD_NAME)) {
        throw new JsonParseException("Expected field name token", parser.getCurrentLocation());
      }

      FieldDescriptor field = fieldLookup.get(parser.getCurrentName());
      if (field == null && !context.handleUnknownProperty(parser, this, builder, parser.getCurrentName())) {
        context.reportUnknownProperty(builder, parser.getCurrentName(), this);
      }
      parser.nextToken();
      setField(builder, field, parser, context);
    }
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
        throw context.mappingException(builder.getClass(), parser.getCurrentToken());
      }
    }

    switch (field.getJavaType()) {
      case INT:
        value = _parseInteger(parser, context);

        if (value == null) {
          new NullProvider(SimpleType.construct(Integer.class), 0).nullValue(context);
        }
        break;
      case LONG:
        value = _parseLong(parser, context);

        if (value == null) {
          new NullProvider(SimpleType.construct(Long.class), 0L).nullValue(context);
        }
        break;
      case FLOAT:
        value = _parseFloat(parser, context);

        if (value == null) {
          new NullProvider(SimpleType.construct(Float.class), 0.0f).nullValue(context);
        }
        break;
      case DOUBLE:
        value = _parseDouble(parser, context);

        if (value == null) {
          new NullProvider(SimpleType.construct(Double.class), 0.0d).nullValue(context);
        }
        break;
      case BOOLEAN:
        value = _parseBoolean(parser, context);

        if (value == null) {
          new NullProvider(SimpleType.construct(Boolean.class), false).nullValue(context);
        }
        break;
      case STRING:
        value = _parseString(parser, context);
        break;
      case BYTE_STRING:
        switch (parser.getCurrentToken()) {
          case VALUE_STRING:
            value = ByteString.copyFrom(context.getBase64Variant().decode(parser.getText()));
            break;
          default:
            throw context.mappingException(ByteString.class, parser.getCurrentToken());
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
          default:
            throw context.mappingException(Enum.class, parser.getCurrentToken());
        }
        break;
      case MESSAGE:
        switch (parser.getCurrentToken()) {
          case START_OBJECT:
            Message.Builder subBuilder = builder.newBuilderForField(field);
            populate(subBuilder, parser, context);
            value = subBuilder.build();
            break;
          default:
            throw context.mappingException(builder.newBuilderForField(field).getClass(), parser.getCurrentToken());
        }
        break;
      default:
        throw context.mappingException(builder.getClass());
    }

    return value;
  }

  private List<Object> readArray(Message.Builder builder, FieldDescriptor field, JsonParser parser,
                                 DeserializationContext context) throws IOException {
    List<Object> values = Lists.newArrayList();
    while (!JsonToken.END_ARRAY.equals(parser.nextToken())) {
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
      public Integer apply(EnumValueDescriptor value) {
        return value.getIndex();
      }
    });

    Collections.sort(indices);

    return "[" + Joiner.on(',').join(indices) + "]";
  }
}
