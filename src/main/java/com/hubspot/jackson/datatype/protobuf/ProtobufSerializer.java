package com.hubspot.jackson.datatype.protobuf;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.PropertyNamingStrategyBase;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.google.protobuf.ByteString;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.EnumValueDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.ExtensionRegistry.ExtensionInfo;
import com.google.protobuf.GeneratedMessageV3.ExtendableMessageOrBuilder;
import com.google.protobuf.MessageOrBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.String.format;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public class ProtobufSerializer extends StdSerializer<MessageOrBuilder> {
  @SuppressFBWarnings(value="SE_BAD_FIELD")
  private final ExtensionRegistryWrapper extensionRegistry;
  private final Map<Class<?>, JsonSerializer<Object>> serializerCache;

  public ProtobufSerializer() {
    this(ExtensionRegistryWrapper.empty());
  }

  public ProtobufSerializer(ExtensionRegistryWrapper extensionRegistry) {
    super(MessageOrBuilder.class);

    this.extensionRegistry = extensionRegistry;
    this.serializerCache = new ConcurrentHashMap<>();
  }

  @Override
  public void serialize(MessageOrBuilder message, JsonGenerator generator, SerializerProvider serializerProvider)
          throws IOException {
    generator.writeStartObject();

    Include include = serializerProvider.getConfig().getSerializationInclusion();
    PropertyNamingStrategyBase namingStrategy =
            new PropertyNamingStrategyWrapper(serializerProvider.getConfig().getPropertyNamingStrategy());

    Descriptor descriptor = message.getDescriptorForType();
    List<FieldDescriptor> fields = new ArrayList<>();
    fields.addAll(descriptor.getFields());
    if (message instanceof ExtendableMessageOrBuilder<?>) {
      for (ExtensionInfo extensionInfo : extensionRegistry.getExtensionsByDescriptor(descriptor)) {
        fields.add(extensionInfo.descriptor);
      }
    }

    for (FieldDescriptor field : fields) {
      if (field.isRepeated()) {
        List<?> valueList = (List<?>) message.getField(field);

        if (!valueList.isEmpty() || writeEmptyArrays(serializerProvider)) {
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
        }
      } else if (message.hasField(field)) {
        generator.writeFieldName(namingStrategy.translate(field.getName()));
        writeValue(field, message.getField(field), generator, serializerProvider);
      } else if (include == Include.ALWAYS) {
        generator.writeFieldName(namingStrategy.translate(field.getName()));
        generator.writeNull();
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
        Class<?> subType = value.getClass();

        JsonSerializer<Object> serializer = serializerCache.get(subType);
        if (serializer == null) {
          serializer = serializerProvider.findValueSerializer(value.getClass(), null);
          serializerCache.put(subType, serializer);
        }

        serializer.serialize(value, generator, serializerProvider);
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
