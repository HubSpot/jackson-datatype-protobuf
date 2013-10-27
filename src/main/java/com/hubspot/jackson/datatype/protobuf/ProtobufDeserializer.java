package com.hubspot.jackson.datatype.protobuf;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.common.base.CaseFormat;
import com.google.common.collect.Lists;
import com.google.protobuf.ByteString;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Message;
import com.google.protobuf.MessageOrBuilder;
import org.apache.commons.codec.binary.Base64;

import java.io.IOException;
import java.util.List;

import static java.lang.String.format;

public class ProtobufDeserializer<T extends Message> extends JsonDeserializer<MessageOrBuilder> {
  private final T defaultInstance;
  private final CaseFormat format;
  private final boolean build;

  @SuppressWarnings("unchecked")
  public ProtobufDeserializer(Class<T> messageType, CaseFormat format, boolean build) throws JsonMappingException {
    try {
      this.defaultInstance = (T) messageType.getMethod("getDefaultInstance").invoke(null);
    } catch (Exception e) {
      throw new JsonMappingException("Unable to get default instance for type " + messageType, e);
    }

    this.format = format;
    this.build = build;
  }

  @Override
  public MessageOrBuilder deserialize(JsonParser parser, DeserializationContext context) throws IOException {
    Message.Builder builder = defaultInstance.newBuilderForType();

    populate(builder, parser);

    if (build) {
      return builder.build();
    } else {
      return builder;
    }
  }

  private void populate(Message.Builder builder, JsonParser parser) throws IOException {
    if (!JsonToken.START_OBJECT.equals(parser.getCurrentToken())) {
      throw new JsonParseException("Expected start object token", parser.getCurrentLocation());
    }

    Descriptor descriptor = builder.getDescriptorForType();

    while (!parser.nextToken().equals(JsonToken.END_OBJECT)) {
      JsonToken token = parser.getCurrentToken();
      if (!token.equals(JsonToken.FIELD_NAME)) {
        throw new JsonParseException("Expected field name token", parser.getCurrentLocation());
      }

      String fieldName = translate(parser.getCurrentName());
      FieldDescriptor field = descriptor.findFieldByName(fieldName);
      if (field == null) {
        throw unrecognizedField(defaultInstance.getClass(), parser);
      }
      parser.nextToken();
      setField(builder, field, parser);
    }
  }

  private String translate(String fieldName) {
    return format.to(CaseFormat.LOWER_UNDERSCORE, fieldName);
  }

  private void setField(Message.Builder builder, FieldDescriptor field, JsonParser parser) throws IOException {
    Object value = readValue(builder, field, parser);

    if (value != null) {
      if (field.isRepeated()) {
        for (Object subValue : (Iterable<?>) value) {
          builder.addRepeatedField(field, subValue);
        }
      } else {
        builder.setField(field, value);
      }
    }
  }

  private Object readValue(Message.Builder builder, FieldDescriptor field, JsonParser parser) throws IOException {
    JsonToken token = parser.getCurrentToken();

    final Object value;
    switch (token) {
      case VALUE_STRING:
        switch (field.getJavaType()) {
          case ENUM:
            value = field.getEnumType().findValueByName(parser.getText());

            if (value == null) {
              throw invalidEnum(field, parser);
            }
            break;
          case STRING:
            value = parser.getText();
            break;
          case BYTE_STRING:
            value = ByteString.copyFrom(Base64.decodeBase64(parser.getText()));
            break;
          default:
            throw invalidToken(field, parser);
        }
        break;
      case VALUE_TRUE:
      case VALUE_FALSE:
        switch (field.getJavaType()) {
          case BOOLEAN:
            value = parser.getBooleanValue();
            break;
          case STRING:
            value = parser.getValueAsString();
            break;
          default:
            throw invalidToken(field, parser);
        }
        break;
      case VALUE_NUMBER_INT:
        switch (field.getJavaType()) {
          case INT:
            value = parser.getIntValue();
            break;
          case LONG:
            value = parser.getLongValue();
            break;
          case DOUBLE:
            value = parser.getDoubleValue();
            break;
          case FLOAT:
            value = parser.getFloatValue();
            break;
          case STRING:
            value = parser.getValueAsString();
            break;
          default:
            throw invalidToken(field, parser);
        }
        break;
      case VALUE_NUMBER_FLOAT:
        switch (field.getJavaType()) {
          case DOUBLE:
            value = parser.getDoubleValue();
            break;
          case FLOAT:
            value = parser.getFloatValue();
            break;
          case STRING:
            value = parser.getValueAsString();
            break;
          default:
            throw invalidToken(field, parser);
        }
        break;
      case START_OBJECT:
        switch (field.getJavaType()) {
          case MESSAGE:
            Message.Builder subBuilder = builder.newBuilderForField(field);
            populate(subBuilder, parser);
            value = subBuilder.build();
            break;
          default:
            throw invalidToken(field, parser);
        }
        break;
      case START_ARRAY:
        if (field.isRepeated()) {
          value = readArray(builder, field, parser);
        } else {
          throw unexpectedArray(field, parser);
        }
        break;
      case VALUE_NULL:
        value = null;
        break;
      default:
        throw parseException("Unexpected JSON token " + token, parser.getCurrentLocation());
    }

    return value;
  }

  private List<Object> readArray(Message.Builder builder, FieldDescriptor field, JsonParser parser) throws IOException {
    List<Object> values = Lists.newArrayList();
    while (!JsonToken.END_ARRAY.equals(parser.nextToken())) {
      Object value = readValue(builder, field, parser);

      if (value != null) {
        values.add(value);
      }
    }
    return values;
  }

  private static JsonParseException unrecognizedField(Class<?> messageType, JsonParser parser) throws IOException {
    String message = format("Unrecognized field '%s' for message %s", parser.getCurrentName(), messageType);
    throw parseException(message, parser.getCurrentLocation());
  }

  private static JsonParseException invalidEnum(FieldDescriptor field, JsonParser parser) throws IOException {
    String message = format("Unrecognized value '%s' for enum %s", parser.getText(), field.getEnumType().getFullName());
    throw parseException(message, parser.getCurrentLocation());
  }

  private static JsonParseException invalidToken(FieldDescriptor field, JsonParser parser) throws IOException {
    String message = format("Cannot deserialize %s out of token %s for field %s", field.getJavaType(),
            parser.getCurrentToken(), field.getFullName());
    throw parseException(message, parser.getCurrentLocation());
  }

  private static JsonParseException unexpectedArray(FieldDescriptor field, JsonParser parser) throws IOException {
    String message = "Cannot deserialize array into non-repeated field " + field.getFullName();
    throw parseException(message, parser.getCurrentLocation());
  }

  private static JsonParseException parseException(String message, JsonLocation location) throws IOException {
    throw new JsonParseException(message, location);
  }
}
