package com.hubspot.jackson.datatype.protobuf;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.google.protobuf.ByteString;
import com.google.protobuf.Descriptors.EnumValueDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.MessageOrBuilder;
import org.apache.commons.codec.binary.Base64;

import java.io.IOException;
import java.util.Map.Entry;

import static java.lang.String.format;

public class ProtobufSerializer extends StdSerializer<MessageOrBuilder> {

  public ProtobufSerializer() {
    super(MessageOrBuilder.class);
  }

  @Override
  public void serialize(MessageOrBuilder message, JsonGenerator generator,
                        SerializerProvider serializerProvider) throws IOException {
    generator.writeStartObject();

    for (Entry<FieldDescriptor, Object> record : message.getAllFields().entrySet()) {
      FieldDescriptor field = record.getKey();
      Object value = record.getValue();

      if (field.isRepeated()) {
        generator.writeArrayFieldStart(field.getName());
        for (Object subValue : ((Iterable<?>) message.getField(field))) {
          writeValue(field, subValue, generator, serializerProvider);
        }
        generator.writeEndArray();
      } else {
        generator.writeFieldName(field.getName());
        writeValue(field, value, generator, serializerProvider);
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
        generator.writeString(((EnumValueDescriptor) value).getName());
        break;
      case BYTE_STRING:
        generator.writeString(Base64.encodeBase64String(((ByteString) value).toByteArray()));
        break;
      case MESSAGE:
        serialize((MessageOrBuilder) value, generator, serializerProvider);
        break;
      default:
        throw unrecognizedType(field);
    }
  }

  private static IOException unrecognizedType(FieldDescriptor field) throws IOException {
    String error = format("Unrecognized java type '%s' for field '%s'", field.getJavaType(), field.getName());
    throw new JsonMappingException(error);
  }
}
