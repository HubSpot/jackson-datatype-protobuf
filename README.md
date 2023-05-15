## UPDATE
We are in the process of decommissioning this repository. We are moving this code into pragma-engine/platform (see https://github.com/pragmaplatform/pragma-engine/tree/updatePom2023) and will no longer be publishing this artifact to nexus. Once this process is complete, any further changes to this library should be made in pragma-engine/platform/jackson-datatype-protobuf.

## Fork
This is a fork of the original jackson-datatype-protobuf. A [pull request](https://github.com/HubSpot/jackson-datatype-protobuf/pull/77) was never merged in, so we forked it to solve [issue 76](https://github.com/HubSpot/jackson-datatype-protobuf/issues/76).

## Overview

Jackson module that adds support for serializing and deserializing Google's 
[Protocol Buffers](https://code.google.com/p/protobuf/) to and from JSON.

## Usage

### Maven dependency

To use module on Maven-based projects, use following dependency:

```xml
<dependency>
  <groupId>gg.pragma</groupId>
  <artifactId>jackson-datatype-protobuf</artifactId>
  <version>0.9.13-LongsAsStrings</version>
</dependency>
```

### Registering module

Registration is done as follows:

```java
ObjectMapper mapper = new ObjectMapper();
mapper.registerModule(new ProtobufModule());
```

after which functionality is available for all normal Jackson operations.

### Interop with Protobuf 3 Canonical JSON Representation

Protobuf 3 specifies a canonical JSON representation (available [here](https://developers.google.com/protocol-buffers/docs/proto3#json)). This library conforms to that representation with a few exceptions:
- int64, fixed64, uint64 are written as JSON numbers instead of strings **by default**
  - ProtobufJacksonConfig now has a field to allow for serialization of these types as strings. See [ProtobufJacksonConfig#L8](src/main/java/com/hubspot/jackson/datatype/protobuf/ProtobufJacksonConfig.java#L8)
  - This can be set by:
  ```
  ObjectMapper mapper = new ObjectMapper();
  mapper.registerModule(new ProtobufModule(ProtobufJacksonConfig.builder().serializeLongsAsStrings(true).build()));
  ```
- `Any` objects don't have any special handling, so the value will be a base64 string, and the type URL field name is `typeUrl` instead of `@type`

### Gotchas

This library assumes that field names in proto files will be lowercase with underscores, as is recommended by the protobuf style guide: https://developers.google.com/protocol-buffers/docs/style#message-and-field-names
> Use underscore_separated_names for field names – for example, song_name.

If your field names don't match this convention, you may need to set a `PropertyNamingStrategy` on your `ObjectMapper` for things to work as expected. For example, if your proto field names are camel case, you could configure your `ObjectMapper` to use `PropertyNamingStrategy.LOWER_CAMEL_CASE`.
