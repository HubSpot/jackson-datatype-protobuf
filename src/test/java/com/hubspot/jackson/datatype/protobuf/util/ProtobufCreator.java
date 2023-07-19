package com.hubspot.jackson.datatype.protobuf.util;

import com.google.protobuf.ByteString;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.EnumValueDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.ExtensionRegistry.ExtensionInfo;
import com.google.protobuf.Message;
import com.google.protobuf.Message.Builder;
import com.hubspot.jackson.datatype.protobuf.ExtensionRegistryWrapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ProtobufCreator {

  private static final Random r = new Random();

  public static <T extends Message> T create(Class<T> messageType) {
    return create(messageType, ExtensionRegistry.getEmptyRegistry());
  }

  public static <T extends Message> T create(
    Class<T> messageType,
    ExtensionRegistry extensionRegistry
  ) {
    return new Creator().create(messageType, extensionRegistry);
  }

  public static <T extends Message> List<T> create(Class<T> messageType, int count) {
    return create(messageType, ExtensionRegistry.getEmptyRegistry(), count);
  }

  public static <T extends Message> List<T> create(
    Class<T> messageType,
    ExtensionRegistry extensionRegistry,
    int count
  ) {
    List<T> messages = new ArrayList<>(count);

    for (int i = 0; i < count; i++) {
      messages.add(create(messageType, extensionRegistry));
    }

    return messages;
  }

  public static <T extends Builder> T createBuilder(Class<T> builderType) {
    return createBuilder(builderType, ExtensionRegistry.getEmptyRegistry());
  }

  @SuppressWarnings("unchecked")
  public static <T extends Builder> T createBuilder(
    Class<T> builderType,
    ExtensionRegistry extensionRegistry
  ) {
    Class<? extends Message> messageType = (Class<? extends Message>) builderType.getDeclaringClass();
    return (T) create(messageType, extensionRegistry).toBuilder();
  }

  public static <T extends Builder> List<T> createBuilder(
    Class<T> builderType,
    int count
  ) {
    return createBuilder(builderType, ExtensionRegistry.getEmptyRegistry(), count);
  }

  public static <T extends Builder> List<T> createBuilder(
    Class<T> builderType,
    ExtensionRegistry extensionRegistry,
    int count
  ) {
    List<T> builders = new ArrayList<>(count);

    for (int i = 0; i < count; i++) {
      builders.add(createBuilder(builderType, extensionRegistry));
    }

    return builders;
  }

  private static class Creator {

    private final Map<Class<? extends Message>, Builder> partiallyBuilt = new HashMap<>();

    private <T extends Message> T create(Class<T> messageType) {
      return create(messageType, ExtensionRegistry.getEmptyRegistry());
    }

    private <T extends Message> T create(
      Class<T> messageType,
      ExtensionRegistry extensionRegistry
    ) {
      return create(messageType, ExtensionRegistryWrapper.wrap(extensionRegistry));
    }

    @SuppressWarnings("unchecked")
    private <T extends Message> T create(
      Class<T> messageType,
      ExtensionRegistryWrapper extensionRegistry
    ) {
      Builder builder = newBuilder(messageType);
      partiallyBuilt.put(messageType, builder);
      populate(builder, extensionRegistry);
      return (T) builder.build();
    }

    private static <T extends Message> Builder newBuilder(Class<T> messageType) {
      try {
        return (Builder) messageType.getMethod("newBuilder").invoke(null);
      } catch (ReflectiveOperationException e) {
        throw new RuntimeException(e);
      }
    }

    private void populate(Builder builder, ExtensionRegistryWrapper extensionRegistry) {
      Descriptor descriptor = builder.getDescriptorForType();

      for (FieldDescriptor field : descriptor.getFields()) {
        if (field.isRepeated()) {
          int count = r.nextInt(5) + 1;
          for (int i = 0; i < count; i++) {
            builder.addRepeatedField(
              field,
              getValue(builder, field, null, extensionRegistry)
            );
          }
        } else {
          builder.setField(field, getValue(builder, field, null, extensionRegistry));
        }
      }

      for (ExtensionInfo extensionInfo : extensionRegistry.getExtensionsByDescriptor(
        descriptor
      )) {
        FieldDescriptor extension = extensionInfo.descriptor;
        Message defaultInstance = extensionInfo.defaultInstance;
        if (extension.isRepeated()) {
          int count = r.nextInt(5) + 1;
          for (int i = 0; i < count; i++) {
            builder.addRepeatedField(
              extension,
              getValue(builder, extension, defaultInstance, extensionRegistry)
            );
          }
        } else {
          builder.setField(
            extension,
            getValue(builder, extension, defaultInstance, extensionRegistry)
          );
        }
      }
    }

    private Object getValue(
      Builder builder,
      FieldDescriptor field,
      Message defaultInstance,
      ExtensionRegistryWrapper extensionRegistry
    ) {
      switch (field.getJavaType()) {
        case INT:
          return r.nextInt();
        case LONG:
          return r.nextLong();
        case FLOAT:
          return r.nextFloat();
        case DOUBLE:
          return r.nextDouble();
        case BOOLEAN:
          return r.nextBoolean();
        case STRING:
          String available = "abcdefghijklmnopqrstuvwxyz0123456789";
          int length = r.nextInt(20) + 1;
          String value = "";
          for (int i = 0; i < length; i++) {
            value += available.charAt(r.nextInt(available.length()));
          }
          return value;
        case BYTE_STRING:
          byte[] bytes = new byte[r.nextInt(20) + 1];
          r.nextBytes(bytes);
          return ByteString.copyFrom(bytes);
        case ENUM:
          List<EnumValueDescriptor> values = field.getEnumType().getValues();
          return values.get(r.nextInt(values.size()));
        case MESSAGE:
          final Class<? extends Message> subMessageType;
          if (field.isExtension()) {
            subMessageType = defaultInstance.getClass();
          } else {
            subMessageType =
              builder.newBuilderForField(field).getDefaultInstanceForType().getClass();
          }

          // Handle recursive relationships by returning a partially populated proto (better than an infinite loop)
          if (partiallyBuilt.containsKey(subMessageType)) {
            return partiallyBuilt.get(subMessageType).build();
          } else {
            return create(subMessageType, extensionRegistry);
          }
        default:
          throw new IllegalArgumentException(
            "Unrecognized field type: " + field.getJavaType()
          );
      }
    }
  }
}
