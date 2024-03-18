package com.hubspot.jackson.datatype.protobuf.builtin.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.ExtensionRegistry.ExtensionInfo;
import com.google.protobuf.Message;
import com.google.protobuf.Message.Builder;
import com.hubspot.jackson.datatype.protobuf.ExtensionRegistryWrapper;
import com.hubspot.jackson.datatype.protobuf.PropertyNamingStrategyWrapper;
import com.hubspot.jackson.datatype.protobuf.ProtobufDeserializer;
import com.hubspot.jackson.datatype.protobuf.ProtobufJacksonConfig;
import com.hubspot.jackson.datatype.protobuf.internal.PropertyNamingCache;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class MessageDeserializer<T extends Message, V extends Builder>
  extends ProtobufDeserializer<T, V> {

  private final Class<T> messageType;

  @SuppressFBWarnings(value = "SE_BAD_FIELD")
  private final ProtobufJacksonConfig config;

  private final PropertyNamingCache propertyNamingCache;

  /**
   * @deprecated use {@link #MessageDeserializer(Class, ProtobufJacksonConfig)} instead
   */
  @Deprecated
  public MessageDeserializer(
    Class<T> messageType,
    ExtensionRegistryWrapper extensionRegistry
  ) {
    this(
      messageType,
      ProtobufJacksonConfig.builder().extensionRegistry(extensionRegistry).build()
    );
  }

  public MessageDeserializer(Class<T> messageType, ProtobufJacksonConfig config) {
    super(messageType);
    this.messageType = messageType;
    this.config = config;
    this.propertyNamingCache =
      PropertyNamingCache.forDescriptor(getDescriptor(), messageType, config);
  }

  @Override
  protected void populate(V builder, JsonParser parser, DeserializationContext context)
    throws IOException {
    JsonToken token = parser.getCurrentToken();
    if (token == JsonToken.START_ARRAY) {
      token = parser.nextToken();
    }

    switch (token) {
      case END_OBJECT:
        return;
      case START_OBJECT:
        token = parser.nextToken();
        if (token == JsonToken.END_OBJECT) {
          return;
        }
        break;
      default:
        break; // make findbugs happy
    }

    final Descriptor descriptor = builder.getDescriptorForType();
    final Function<String, FieldDescriptor> fieldLookup =
      propertyNamingCache.forDeserialization(context.getConfig());
    final Map<String, ExtensionInfo> extensionLookup;
    if (descriptor.isExtendable()) {
      extensionLookup = buildExtensionLookup(descriptor, context);
    } else {
      extensionLookup = Collections.emptyMap();
    }

    do {
      if (!token.equals(JsonToken.FIELD_NAME)) {
        throw reportWrongToken(JsonToken.FIELD_NAME, context, "");
      }

      String name = parser.getCurrentName();
      FieldDescriptor field = fieldLookup.apply(name);
      Message defaultInstance = null;
      if (field == null) {
        ExtensionInfo extensionInfo = extensionLookup.get(name);
        if (extensionInfo != null) {
          field = extensionInfo.descriptor;
          defaultInstance = extensionInfo.defaultInstance;
        }
      }

      if (field == null) {
        context.handleUnknownProperty(parser, this, builder, name);
        parser.nextToken();
        parser.skipChildren();
        continue;
      }

      parser.nextToken();
      setField(builder, field, defaultInstance, parser, context);
    } while ((token = parser.nextToken()) != JsonToken.END_OBJECT);
  }

  private Map<String, ExtensionInfo> buildExtensionLookup(
    Descriptor descriptor,
    DeserializationContext context
  ) {
    PropertyNamingStrategyWrapper namingStrategy = new PropertyNamingStrategyWrapper(
      messageType,
      context.getConfig()
    );

    Map<String, ExtensionInfo> extensionLookup = new HashMap<>();
    for (ExtensionInfo extensionInfo : config
      .extensionRegistry()
      .getExtensionsByDescriptor(descriptor)) {
      extensionLookup.put(
        namingStrategy.translate(extensionInfo.descriptor.getName()),
        extensionInfo
      );
    }

    return extensionLookup;
  }

  private void setField(
    V builder,
    FieldDescriptor field,
    Message defaultInstance,
    JsonParser parser,
    DeserializationContext context
  ) throws IOException {
    if (field.isMapField()) {
      List<Message> entries = readMap(builder, field, parser, context);
      for (Message entry : entries) {
        builder.addRepeatedField(field, entry);
      }
    } else if (field.isRepeated()) {
      List<Object> values = readArray(builder, field, defaultInstance, parser, context);

      for (Object value : values) {
        builder.addRepeatedField(field, value);
      }
    } else {
      Object value = readValue(builder, field, defaultInstance, parser, context);

      if (value != null) {
        builder.setField(field, value);
      }
    }
  }

  private AssertionError reportWrongToken(
    JsonToken expected,
    DeserializationContext context,
    String message
  ) throws JsonMappingException {
    context.reportWrongTokenException(this, expected, message);
    // the previous method should have thrown
    throw new AssertionError();
  }
}
