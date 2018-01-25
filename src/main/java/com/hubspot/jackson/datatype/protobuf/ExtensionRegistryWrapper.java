package com.hubspot.jackson.datatype.protobuf;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.ExtensionRegistry.ExtensionInfo;

public class ExtensionRegistryWrapper {
  private final Function<Descriptor, Set<ExtensionInfo>> extensionFunction;

  private ExtensionRegistryWrapper() {
    this.extensionFunction = new Function<Descriptor, Set<ExtensionInfo>>() {

      @Override
      public Set<ExtensionInfo> apply(Descriptor descriptor) {
        return Collections.emptySet();
      }
    };
  }

  private ExtensionRegistryWrapper(final ExtensionRegistry extensionRegistry) {
    this.extensionFunction = new Function<Descriptor, Set<ExtensionInfo>>() {
      private final Map<Descriptor, Set<ExtensionInfo>> extensionCache = new ConcurrentHashMap<>();

      @Override
      public Set<ExtensionInfo> apply(Descriptor descriptor) {
        Set<ExtensionInfo> cached = extensionCache.get(descriptor);
        if (cached != null) {
          return cached;
        }

        Set<ExtensionInfo> extensions = extensionRegistry.getAllImmutableExtensionsByExtendedType(descriptor.getFullName());
        extensionCache.put(descriptor, extensions);
        return extensions;
      }
    };
  }

  public static ExtensionRegistryWrapper wrap(ExtensionRegistry extensionRegistry) {
    return new ExtensionRegistryWrapper(extensionRegistry);
  }

  public static ExtensionRegistryWrapper empty() {
    return new ExtensionRegistryWrapper();
  }

  public Set<ExtensionInfo> findExtensionsByDescriptor(Descriptor descriptor) {
    return extensionFunction.apply(descriptor);
  }

  private interface Function<T, V> {
    V apply(T t);
  }
}
