package com.hubspot.jackson.datatype.protobuf;

import static java.lang.String.format;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.google.protobuf.ByteString;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.EnumValueDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Message;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.NullValue;
import com.hubspot.jackson.datatype.protobuf.internal.Constants;
import com.hubspot.jackson.datatype.protobuf.internal.Types;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class ProtobufSerializer<T extends MessageOrBuilder> extends StdSerializer<T> {
  private static final String NULL_VALUE_FULL_NAME = NullValue.getDescriptor().getFullName();

  private final ProtobufJacksonConfig config;
  private final Map<Class<?>, JsonSerializer<Object>> serializerCache;

  /**
   * @deprecated use {@link #ProtobufSerializer(Class, ProtobufJacksonConfig)}
   */
  @Deprecated
  public ProtobufSerializer(Class<T> protobufType) {
    this(protobufType, ProtobufJacksonConfig.getDefaultInstance());
  }

  public ProtobufSerializer(Class<T> protobufType, ProtobufJacksonConfig config) {
    super(protobufType);

    this.config = config;
    this.serializerCache = new ConcurrentHashMap<>();
  }

  @Override
  public abstract void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException;

  @SuppressWarnings("unchecked")
  protected void writeMap(
          FieldDescriptor field,
          Object entries,
          JsonGenerator generator,
          SerializerProvider serializerProvider
  ) throws IOException {
    Descriptor entryDescriptor = field.getMessageType();
    FieldDescriptor keyDescriptor = entryDescriptor.findFieldByName("key");
    FieldDescriptor valueDescriptor = entryDescriptor.findFieldByName("value");
    generator.writeStartObject();
    for (Message entry : (List<? extends Message>) entries) {
      // map keys can only be integers or strings so this should be fine
      generator.writeFieldName(entry.getField(keyDescriptor).toString());
      Object value = entry.getField(valueDescriptor);
      // map values can't be maps or repeated so this should be fine
      writeValue(valueDescriptor, value, generator, serializerProvider);
    }
    generator.writeEndObject();
  }

  protected ProtobufJacksonConfig getConfig() {
    return config;
  }

  protected void writeValue(
          FieldDescriptor field,
          Object value,
          JsonGenerator generator,
          SerializerProvider serializerProvider
  ) throws IOException {
    switch (field.getJavaType()) {
      case INT:
        writeInt(field, (int) value, generator);
        break;
      case LONG:
        writeLong(field, (long) value, generator);
        break;
      case FLOAT:
        generator.writeNumber((float) value);
        break;
      case DOUBLE:
        generator.writeNumber((double) value);
        break;
      case BOOLEAN:
        generator.writeBoolean((boolean) value);
        break;
      case STRING:
        generator.writeString((String) value);
        break;
      case ENUM:
        EnumValueDescriptor enumDescriptor = (EnumValueDescriptor) value;

        // special-case NullValue
        if (NULL_VALUE_FULL_NAME.equals(enumDescriptor.getType().getFullName())) {
          generator.writeNull();
        } else if (writeEnumsUsingIndex(serializerProvider)) {
          generator.writeNumber(enumDescriptor.getNumber());
        } else {
          generator.writeString(enumDescriptor.getName());
        }
        break;
      case BYTE_STRING:
        generator.writeString(serializerProvider.getConfig().getBase64Variant().encode(((ByteString) value).toByteArray()));
        break;
      case MESSAGE:
        Class<?> subType = value.getClass();

        JsonSerializer<Object> serializer = serializerCache.get(subType);
        if (serializer == null) {
          serializer = serializerProvider.findValueSerializer(value.getClass(), null);
          serializerCache.put(subType, serializer);
        }

        serializer.serialize(value, generator, serializerProvider);
        break;
      default:
        throw unrecognizedType(field, generator);
    }
  }

  private void writeInt(FieldDescriptor field, int value, JsonGenerator generator) throws IOException {
    if (
      value < 0 &&
      config.properUnsignedNumberSerialization() &&
      Types.isUnsigned(field.getType())
    ) {
      long unsignedValue = value & Constants.MAX_UINT32;
      generator.writeNumber(unsignedValue);
    } else {
      generator.writeNumber(value);
    }
  }

  private void writeLong(FieldDescriptor field, long value, JsonGenerator generator) throws IOException {
    if (
      value < 0 &&
      config.properUnsignedNumberSerialization() &&
      Types.isUnsigned(field.getType())
    ) {
      BigInteger unsignedValue = BigInteger.valueOf(value & Long.MAX_VALUE).setBit(Long.SIZE - 1);
      if (config.serializeLongsAsString()) {
        generator.writeString(unsignedValue.toString());
      } else {
        generator.writeNumber(unsignedValue);
      }
    } else if (config.serializeLongsAsString()) {
      generator.writeString(Long.toString(value));
    } else {
      generator.writeNumber(value);
    }
  }

  private static boolean writeEnumsUsingIndex(SerializerProvider config) {
    return config.isEnabled(SerializationFeature.WRITE_ENUMS_USING_INDEX);
  }

  private static IOException unrecognizedType(FieldDescriptor field, JsonGenerator generator) throws IOException {
    String error = format("Unrecognized java type '%s' for field %s", field.getJavaType(), field.getFullName());
    throw new JsonGenerationException(error, generator);
  }
}
