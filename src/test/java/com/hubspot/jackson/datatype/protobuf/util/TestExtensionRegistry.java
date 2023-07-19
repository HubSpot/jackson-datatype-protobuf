package com.hubspot.jackson.datatype.protobuf.util;

import com.google.common.collect.Iterables;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor.JavaType;
import com.google.protobuf.ExtensionRegistry;
import com.hubspot.jackson.datatype.protobuf.util.TestExtensions.AllExtensions;
import com.hubspot.jackson.datatype.protobuf.util.TestExtensions.RepeatedExtensions;
import com.hubspot.jackson.datatype.protobuf.util.TestProtobuf.Nested;

public class TestExtensionRegistry {

  public static ExtensionRegistry getInstance() {
    ExtensionRegistry extensionRegistry = ExtensionRegistry.newInstance();
    Iterable<FieldDescriptor> extensionDescriptors = Iterables.concat(
      AllExtensions.getDescriptor().getExtensions(),
      RepeatedExtensions.getDescriptor().getExtensions()
    );

    for (FieldDescriptor extension : extensionDescriptors) {
      if (extension.getJavaType() == JavaType.MESSAGE) {
        extensionRegistry.add(extension, Nested.getDefaultInstance());
      } else {
        extensionRegistry.add(extension);
      }
    }

    return extensionRegistry;
  }
}
