package com.hubspot.jackson3.datatype.protobuf.internal;

import com.google.protobuf.Descriptors.FieldDescriptor.Type;

public class Types {

  public static boolean isUnsigned(Type type) {
    return (
      type == Type.FIXED32 ||
      type == Type.UINT32 ||
      type == Type.FIXED64 ||
      type == Type.UINT64
    );
  }
}
