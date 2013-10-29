package com.hubspot.jackson.test.util;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.protobuf.ByteString;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.EnumValueDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Message;
import com.google.protobuf.Message.Builder;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class ProtobufCreator {
  private static final Random r = new Random();

  public static <T extends Message> T create(Class<T> messageType) {
    return new Creator().create(messageType);
  }

  public static <T extends Message> List<T> create(Class<T> messageType, int count) {
    List<T> messages = Lists.newArrayListWithCapacity(count);

    for (int i = 0; i < count; i++) {
      messages.add(create(messageType));
    }

    return messages;
  }

  @SuppressWarnings("unchecked")
  public static <T extends Builder> T createBuilder(Class<T> builderType) {
    Class<? extends Message> messageType = (Class<? extends Message>) builderType.getDeclaringClass();
    return (T) create(messageType).toBuilder();
  }

  public static <T extends Builder> List<T> createBuilder(Class<T> builderType, int count) {
    List<T> builders = Lists.newArrayListWithCapacity(count);

    for (int i = 0; i < count; i++) {
      builders.add(createBuilder(builderType));
    }

    return builders;
  }

  private static class Creator {
    private final Map<Class<? extends Message>, Builder> partiallyBuilt = Maps.newHashMap();

    @SuppressWarnings("unchecked")
    private <T extends Message> T create(Class<T> messageType) {
      Builder builder = newBuilder(messageType);
      partiallyBuilt.put(messageType, builder);
      populate(builder);
      return (T) builder.build();
    }

    private static <T extends Message> Builder newBuilder(Class<T> messageType) {
      try {
        return (Builder) messageType.getMethod("newBuilder").invoke(null);
      } catch (Exception e) {
        throw Throwables.propagate(e);
      }
    }

    private void populate(Builder builder) {
      Descriptor descriptor = builder.getDescriptorForType();

      for (FieldDescriptor field : descriptor.getFields()) {
        if (field.isRepeated()) {
          int count = r.nextInt(5) + 1;
          for (int i = 0; i < count; i++) {
            builder.addRepeatedField(field, getValue(builder, field));
          }
        } else {
          builder.setField(field, getValue(builder, field));
        }
      }
    }

    private Object getValue(Builder builder, FieldDescriptor field) {
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
          Class<? extends Message> subMessageType = builder.newBuilderForField(field).getDefaultInstanceForType().getClass();
          // Handle recursive relationships by returning a partially populated proto (better than an infinite loop)
          if (partiallyBuilt.containsKey(subMessageType)) {
            return partiallyBuilt.get(subMessageType).build();
          } else {
            return create(subMessageType);
          }
        default:
          throw new IllegalArgumentException("Unrecognized field type: " + field.getJavaType());
      }
    }
  }

}
