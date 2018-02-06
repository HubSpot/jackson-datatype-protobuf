package com.hubspot.jackson.datatype.protobuf.builtin.serializers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.PropertyNamingStrategyBase;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Descriptors.FileDescriptor.Syntax;
import com.google.protobuf.ExtensionRegistry.ExtensionInfo;
import com.google.protobuf.GeneratedMessageV3.ExtendableMessageOrBuilder;
import com.google.protobuf.MessageOrBuilder;
import com.hubspot.jackson.datatype.protobuf.ExtensionRegistryWrapper;
import com.hubspot.jackson.datatype.protobuf.PropertyNamingStrategyWrapper;
import com.hubspot.jackson.datatype.protobuf.ProtobufSerializer;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public class MessageSerializer extends ProtobufSerializer<MessageOrBuilder> {
  @SuppressFBWarnings(value="SE_BAD_FIELD")
  private final ExtensionRegistryWrapper extensionRegistry;

  public MessageSerializer(ExtensionRegistryWrapper extensionRegistry) {
    super(MessageOrBuilder.class);

    this.extensionRegistry = extensionRegistry;
  }

  @Override
  public void serialize(
          MessageOrBuilder message,
          JsonGenerator generator,
          SerializerProvider serializerProvider
  ) throws IOException {
    generator.writeStartObject();

    boolean proto3 = message.getDescriptorForType().getFile().getSyntax() == Syntax.PROTO3;
    Include include = serializerProvider.getConfig().getSerializationInclusion();
    boolean includeAllFields = include == Include.ALWAYS || (proto3 && include == Include.NON_NULL);
    PropertyNamingStrategyBase namingStrategy =
            new PropertyNamingStrategyWrapper(serializerProvider.getConfig().getPropertyNamingStrategy());

    Descriptor descriptor = message.getDescriptorForType();
    List<FieldDescriptor> fields = new ArrayList<>(descriptor.getFields());
    if (message instanceof ExtendableMessageOrBuilder<?>) {
      for (ExtensionInfo extensionInfo : extensionRegistry.getExtensionsByDescriptor(descriptor)) {
        fields.add(extensionInfo.descriptor);
      }
    }

    for (FieldDescriptor field : fields) {
      if (field.isMapField()) {
        generator.writeFieldName(namingStrategy.translate(field.getName()));
        writeMap(field, message.getField(field), generator, serializerProvider);
      } else if (field.isRepeated()) {
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
      } else if (includeAllFields && field.getContainingOneof() == null) {
        generator.writeFieldName(namingStrategy.translate(field.getName()));
        generator.writeNull();
      }
    }

    generator.writeEndObject();
  }

  private static boolean writeEmptyArrays(SerializerProvider config) {
    return config.isEnabled(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS);
  }

  private static boolean writeSingleElementArraysUnwrapped(SerializerProvider config) {
    return config.isEnabled(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED);
  }
}
