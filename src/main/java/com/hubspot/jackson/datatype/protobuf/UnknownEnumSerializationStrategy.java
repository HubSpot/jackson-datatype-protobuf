package com.hubspot.jackson.datatype.protobuf;

import com.google.protobuf.Descriptors.EnumValueDescriptor;

@FunctionalInterface
public interface UnknownEnumSerializationStrategy {

  EnumValueDescriptor handleUnknownEnumValue(EnumValueDescriptor descriptor);

  UnknownEnumSerializationStrategy SERIALIZE = descriptor -> descriptor;

  UnknownEnumSerializationStrategy FAIL =
      descriptor -> {
        throw new IllegalArgumentException(
            "Unable to serialize an unknown enum value " + descriptor.getFullName());
      };
}
