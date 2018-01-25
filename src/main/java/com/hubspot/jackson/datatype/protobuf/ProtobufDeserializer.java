package com.hubspot.jackson.datatype.protobuf;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nonnull;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.PropertyNamingStrategyBase;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.type.SimpleType;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.protobuf.ByteString;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.EnumDescriptor;
import com.google.protobuf.Descriptors.EnumValueDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.ExtensionRegistry.ExtensionInfo;
import com.google.protobuf.GeneratedMessageV3.ExtendableMessageOrBuilder;
import com.google.protobuf.Message;
import com.google.protobuf.MessageOrBuilder;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public class ProtobufDeserializer<T extends Message> extends StdDeserializer<MessageOrBuilder> {
  private final T defaultInstance;
  private final boolean build;
  @SuppressFBWarnings(value="SE_BAD_FIELD")
  private final ExtensionRegistryWrapper extensionRegistry;
  @SuppressFBWarnings(value="SE_BAD_FIELD")
  private final Map<FieldDescriptor, JsonDeserializer<Object>> deserializerCache;

  public ProtobufDeserializer(Class<T> messageType, boolean build) throws JsonMappingException {
    this(messageType, build, ExtensionRegistryWrapper.empty());
  }

  @SuppressWarnings("unchecked")
  public ProtobufDeserializer(Class<T> messageType, boolean build,
                              ExtensionRegistryWrapper extensionRegistry) throws JsonMappingException {
    super(messageType);

    try {
      this.defaultInstance = (T) messageType.getMethod("getDefaultInstance").invoke(null);
    } catch (Exception e) {
      throw new JsonMappingException("Unable to get default instance for type " + messageType, e);
    }

    this.build = build;
    this.extensionRegistry = extensionRegistry;
    this.deserializerCache = new ConcurrentHashMap<>();
  }

  @Override
  public MessageOrBuilder deserialize(JsonParser parser, DeserializationContext context) throws IOException {
    Message.Builder builder = defaultInstance.newBuilderForType();

    populate(builder, parser, context);

    if (build) {
      return builder.build();
    } else {
      return builder;
    }
  }

  private void populate(Message.Builder builder, JsonParser parser, DeserializationContext context)
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
    final Map<String, FieldDescriptor> fieldLookup = buildFieldLookup(descriptor, context);
    final Map<String, ExtensionInfo> extensionLookup;
    if (builder instanceof ExtendableMessageOrBuilder<?>) {
      extensionLookup = buildExtensionLookup(descriptor, context);
    } else {
      extensionLookup = Collections.emptyMap();
    }

    do {
      if (!token.equals(JsonToken.FIELD_NAME)) {
        throw context.wrongTokenException(parser, JsonToken.FIELD_NAME, "");
      }

      String name = parser.getCurrentName();
      FieldDescriptor field = fieldLookup.get(name);
      Message defaultInstance = null;
      if (field == null) {
        ExtensionInfo extensionInfo = extensionLookup.get(name);
        if (extensionInfo != null) {
          field = extensionInfo.descriptor;
          defaultInstance = extensionInfo.defaultInstance;
        }
      }

      if (field == null) {
        if (!context.handleUnknownProperty(parser, this, builder, name)) {
          context.reportUnknownProperty(builder, name, this);
        }

        parser.nextToken();
        parser.skipChildren();
        continue;
      }

      parser.nextToken();
      setField(builder, field, defaultInstance, parser, context);
    } while ((token = parser.nextToken()) != JsonToken.END_OBJECT);
  }

  private Map<String, FieldDescriptor> buildFieldLookup(Descriptor descriptor, DeserializationContext context) {
    PropertyNamingStrategyBase namingStrategy =
            new PropertyNamingStrategyWrapper(context.getConfig().getPropertyNamingStrategy());

    Map<String, FieldDescriptor> fieldLookup = new HashMap<>();
    for (FieldDescriptor field : descriptor.getFields()) {
      fieldLookup.put(namingStrategy.translate(field.getName()), field);
    }

    return fieldLookup;
  }

  private Map<String, ExtensionInfo> buildExtensionLookup(Descriptor descriptor, DeserializationContext context) {
    PropertyNamingStrategyBase namingStrategy =
            new PropertyNamingStrategyWrapper(context.getConfig().getPropertyNamingStrategy());

    Map<String, ExtensionInfo> extensionLookup = new HashMap<>();
    for (ExtensionInfo extensionInfo : extensionRegistry.findExtensionsByDescriptor(descriptor)) {
      extensionLookup.put(namingStrategy.translate(extensionInfo.descriptor.getName()), extensionInfo);
    }

    return extensionLookup;
  }

  private void setField(Message.Builder builder, FieldDescriptor field, Message defaultInstance, JsonParser parser,
                        DeserializationContext context) throws IOException {
    Object value = readValue(builder, field, defaultInstance, parser, context);

    if (value != null) {
      if (field.isRepeated()) {
        if (value instanceof Iterable) {
          for (Object subValue : (Iterable<?>) value) {
            builder.addRepeatedField(field, subValue);
          }
        } else if (context.isEnabled(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)) {
          builder.addRepeatedField(field, value);
        } else {
          throw mappingException(field, context);
        }
      } else {
        builder.setField(field, value);
      }
    }
  }

  private void checkNullReturn(FieldDescriptor field, DeserializationContext context) throws JsonProcessingException {
    if (context.isEnabled(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)) {
      throw context.mappingException("Can not map JSON null into primitive field " + field.getFullName()
          + " (set DeserializationConfig.DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES to 'false' to allow)");
    }
  }

  private Object readValue(Message.Builder builder, FieldDescriptor field, Message defaultInstance, JsonParser parser,
                           DeserializationContext context) throws IOException {
    if (parser.getCurrentToken() == JsonToken.START_ARRAY) {
      if (field.isRepeated()) {
        return readArray(builder, field, defaultInstance, parser, context);
      } else {
        throw mappingException(field, context);
      }
    }

    if (parser.getCurrentToken() == JsonToken.VALUE_NULL) {
      switch (field.getJavaType()) {
        case INT:
        case LONG:
        case FLOAT:
        case DOUBLE:
        case BOOLEAN:
          checkNullReturn(field, context);
          return null; // could just fall through, but findbugs is dumb
        default:
          return null;
      }
    }

    switch (field.getJavaType()) {
      case INT:
        return _parseIntPrimitive(parser, context);
      case LONG:
        return _parseLongPrimitive(parser, context);
      case FLOAT:
        return _parseFloatPrimitive(parser, context);
      case DOUBLE:
        return _parseDoublePrimitive(parser, context);
      case BOOLEAN:
        return _parseBooleanPrimitive(parser, context);
      case STRING:
        switch (parser.getCurrentToken()) {
          case VALUE_STRING:
            return parser.getText();
          default:
            return _parseString(parser, context);
        }
      case BYTE_STRING:
        switch (parser.getCurrentToken()) {
          case VALUE_STRING:
            return ByteString.copyFrom(context.getBase64Variant().decode(parser.getText()));
          default:
            throw mappingException(field, context);
        }
      case ENUM:
        final EnumValueDescriptor enumValueDescriptor;
        switch (parser.getCurrentToken()) {
          case VALUE_STRING:
            enumValueDescriptor = field.getEnumType().findValueByName(parser.getText());

            if (enumValueDescriptor == null && !ignorableEnum(parser.getText().trim(), context)) {
              throw context.weirdStringException(parser.getText(), field.getEnumType().getClass(),
                      "value not one of declared Enum instance names");
            }

            return enumValueDescriptor;
          case VALUE_NUMBER_INT:
            if (allowNumbersForEnums(context)) {
              enumValueDescriptor = field.getEnumType().findValueByNumber(parser.getIntValue());

              if (enumValueDescriptor == null && !ignoreUnknownEnums(context)) {
                throw context.weirdNumberException(parser.getIntValue(), field.getEnumType().getClass(),
                        "index value outside legal index range " + indexRange(field.getEnumType()));
              }

              return enumValueDescriptor;
            } else {
              throw context.mappingException("Not allowed to deserialize Enum value out of JSON number " +
                      "(disable DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS to allow)");
            }
          default:
            throw mappingException(field, context);
        }
      case MESSAGE:
        switch (parser.getCurrentToken()) {
          case START_OBJECT:
            JsonDeserializer<Object> deserializer = deserializerCache.get(field);
            if (deserializer == null) {
              final Class<?> subType;
              if (defaultInstance == null) {
                Message.Builder subBuilder = builder.newBuilderForField(field);
                subType = subBuilder.getDefaultInstanceForType().getClass();
              } else {
                subType = defaultInstance.getClass();
              }

              JavaType type = SimpleType.construct(subType);
              deserializer = context.findContextualValueDeserializer(type, null);
              deserializerCache.put(field, deserializer);
            }

            return deserializer.deserialize(parser, context);
          default:
            throw mappingException(field, context);
        }
      default:
        throw mappingException(field, context);
    }
  }

  private List<Object> readArray(Message.Builder builder, FieldDescriptor field, Message defaultInstance, JsonParser parser,
                                 DeserializationContext context) throws IOException {
    List<Object> values = Lists.newArrayList();
    while (parser.nextToken() != JsonToken.END_ARRAY) {
      Object value = readValue(builder, field, defaultInstance, parser, context);

      if (value != null) {
        values.add(value);
      }
    }
    return values;
  }

  private static boolean ignorableEnum(String value, DeserializationContext context) {
    return (acceptEmptyStringAsNull(context) && value.length() == 0) || ignoreUnknownEnums(context);
  }

  private static boolean acceptEmptyStringAsNull(DeserializationContext context) {
    return context.isEnabled(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
  }

  private static boolean allowNumbersForEnums(DeserializationContext context) {
    return !context.isEnabled(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS);
  }

  private static boolean ignoreUnknownEnums(DeserializationContext context) {
    return context.isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL);
  }

  private static String indexRange(EnumDescriptor field) {
    List<Integer> indices = Lists.transform(field.getValues(), new Function<EnumValueDescriptor, Integer>() {
      @Override
      public Integer apply(@Nonnull EnumValueDescriptor value) {
        return value.getIndex();
      }
    });

    // Guava returns non-modifiable list
    indices = Lists.newArrayList(indices);

    Collections.sort(indices);

    return "[" + Joiner.on(',').join(indices) + "]";
  }

  private static JsonMappingException mappingException(FieldDescriptor field, DeserializationContext context)
          throws IOException {
    JsonToken token = context.getParser().getCurrentToken();
    String message = "Can not deserialize instance of " + field.getJavaType() + " out of " + token + " token";
    throw context.mappingException(message);
  }
}
