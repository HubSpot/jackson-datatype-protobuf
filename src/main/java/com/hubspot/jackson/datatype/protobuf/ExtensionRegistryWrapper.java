package com.hubspot.jackson.datatype.protobuf;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.ExtensionRegistry.ExtensionInfo;

public class ExtensionRegistryWrapper {
  private final Map<Descriptor, List<ExtensionInfo>> extensionInfoByDescriptor;

  private ExtensionRegistryWrapper() {
    this.extensionInfoByDescriptor = Collections.emptyMap();
  }

  private ExtensionRegistryWrapper(ExtensionRegistry extensionRegistry) {
    this.extensionInfoByDescriptor = computeExtensionInfoByDescriptor(extensionRegistry);
  }

  public static ExtensionRegistryWrapper wrap(ExtensionRegistry extensionRegistry) {
    return new ExtensionRegistryWrapper(extensionRegistry);
  }

  public static ExtensionRegistryWrapper empty() {
    return new ExtensionRegistryWrapper();
  }

  public List<ExtensionInfo> findExtensionsByDescriptor(Descriptor descriptor) {
    List<ExtensionInfo> extensions = extensionInfoByDescriptor.get(descriptor);
    return extensions == null ? Collections.<ExtensionInfo>emptyList() : extensions;
  }

  private static Map<Descriptor, List<ExtensionInfo>> computeExtensionInfoByDescriptor(ExtensionRegistry extensionRegistry) {
    Map<Descriptor, List<ExtensionInfo>> extensionInfoByDescriptor = new HashMap<>();
    for (ExtensionInfo extensionInfo : extractExtensionInfo(extensionRegistry)) {
      Descriptor descriptor = extensionInfo.descriptor.getContainingType();
      if (!extensionInfoByDescriptor.containsKey(descriptor)) {
        extensionInfoByDescriptor.put(descriptor, new ArrayList<ExtensionInfo>());
      }

      extensionInfoByDescriptor.get(descriptor).add(extensionInfo);
    }

    return extensionInfoByDescriptor;
  }

  @SuppressWarnings("unchecked")
  private static Collection<ExtensionInfo> extractExtensionInfo(ExtensionRegistry extensionRegistry) {
    try {
      Field field = ExtensionRegistry.class.getDeclaredField("extensionsByName");
      field.setAccessible(true);
      Map<String, ExtensionInfo> extensionInfoMap = (Map<String, ExtensionInfo>) field.get(extensionRegistry);
      return extensionInfoMap.values();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
