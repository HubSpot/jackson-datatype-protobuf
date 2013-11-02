package com.hubspot.jackson.datatype.protobuf;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.PropertyNamingStrategyBase;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.google.protobuf.ByteString;
import com.google.protobuf.Descriptors.EnumValueDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.MessageOrBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;

import static java.lang.String.format;

public class ProtobufSerializer extends StdSerializer<MessageOrBuilder> {

  public ProtobufSerializer() {
    super(MessageOrBuilder.class);
  }

  @Override
  public void serialize(MessageOrBuilder message, JsonGenerator generator, SerializerProvider serializerProvider)
          throws IOException {
    generator.writeStartObject();

    PropertyNamingStrategyBase namingStrategy =
            new PropertyNamingStrategyWrapper(serializerProvider.getConfig().getPropertyNamingStrategy());
    for (Entry<FieldDescriptor, Object> record : message.getAllFields().entrySet()) {
      FieldDescriptor field = record.getKey();
      Object value = record.getValue();

      if (field.isRepeated()) {
        List<?> valueList = (List<?>) value;

        if (valueList.size() == 1 && writeSingleElementArraysUnwrapped(serializerProvider)) {
          generator.writeFieldName(namingStrategy.translate(field.getName()));
          writeValue(field, valueList.get(0), generator, serializerProvider);
        } else {
          generator.writeArrayFieldStart(namingStrategy.translate(field.getName()));
          for (Object subValue : valueList) {
            writeValue(field, subValue, generator, serializerProvider);
          }
          generator.writeEndArray();
        }
      } else {
        generator.writeFieldName(namingStrategy.translate(field.getName()));
        writeValue(field, value, generator, serializerProvider);
      }
    }

    if (writeEmptyArrays(serializerProvider)) {
      for (FieldDescriptor field : message.getDescriptorForType().getFields()) {
        if (field.isRepeated()) {
          List<?> valueList = (List<?>) message.getField(field);

          if (valueList.isEmpty()) {
            generator.writeArrayFieldStart(namingStrategy.translate(field.getName()));
            generator.writeEndArray();
          }
        }
      }
    }

    generator.writeEndObject();
  }

  private void writeValue(FieldDescriptor field, Object value, JsonGenerator generator,
                          SerializerProvider serializerProvider) throws IOException {
    switch (field.getJavaType()) {
      case INT:
        generator.writeNumber((Integer) value);
        break;
      case LONG:
        generator.writeNumber((Long) value);
        break;
      case FLOAT:
        generator.writeNumber((Float) value);
        break;
      case DOUBLE:
        generator.writeNumber((Double) value);
        break;
      case BOOLEAN:
        generator.writeBoolean((Boolean) value);
        break;
      case STRING:
        generator.writeString((String) value);
        break;
      case ENUM:
        EnumValueDescriptor enumDescriptor = (EnumValueDescriptor) value;

        if (writeEnumsUsingIndex(serializerProvider)) {
          generator.writeNumber(enumDescriptor.getNumber());
        } else {
          generator.writeString(enumDescriptor.getName());
        }
        break;
      case BYTE_STRING:
        generator.writeString(serializerProvider.getConfig().getBase64Variant().encode(((ByteString) value).toByteArray()));
        break;
      case MESSAGE:
        serialize((MessageOrBuilder) value, generator, serializerProvider);
        break;
      default:
        throw unrecognizedType(field);
    }
  }

  private static boolean writeEmptyArrays(SerializerProvider config) {
    return config.isEnabled(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS);
  }

  private static boolean writeSingleElementArraysUnwrapped(SerializerProvider config) {
    return config.isEnabled(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED);
  }

  private static boolean writeEnumsUsingIndex(SerializerProvider config) {
    return config.isEnabled(SerializationFeature.WRITE_ENUMS_USING_INDEX);
  }

  private static IOException unrecognizedType(FieldDescriptor field) throws IOException {
    String error = format("Unrecognized java type '%s' for field %s", field.getJavaType(), field.getFullName());
    throw new JsonMappingException(error);
  }
}
