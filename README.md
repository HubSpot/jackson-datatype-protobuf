## Overview

Jackson module that adds support for serializing and deserializing Google's 
[Protocol Buffers](https://code.google.com/p/protobuf/) to and from JSON.

## Usage

### Maven dependency

To use module on Maven-based projects, use following dependency:

```xml
<dependency>
  <groupId>com.hubspot.jackson</groupId>
  <artifactId>jackson-datatype-protobuf</artifactId>
  <version><!-- see table below --></version>
</dependency>
```

### Versions

Starting with version 0.9.12, only Jackson 2.9+ is supported. If you're using Jackson 2.9 or newer, you can use the latest version of this library in Maven Central ([link](https://search.maven.org/artifact/com.hubspot.jackson/jackson-datatype-protobuf)).

If you're using a version of Jackson prior to 2.9, you can use the last release before support was dropped:

| Jackson 2.7.x | Jackson 2.8.x |
| ------------- | ------------- |
| 0.9.11-jackson2.7 | 0.9.11-jackson2.8 

### Registering module

Registration is done as follows:

```java
ObjectMapper mapper = new ObjectMapper();
mapper.registerModule(new ProtobufModule());
```

after which functionality is available for all normal Jackson operations.

### Interop with Protobuf 3 Canonical JSON Representation

Protobuf 3 specifies a canonical JSON representation (available [here](https://developers.google.com/protocol-buffers/docs/proto3#json)). This library conforms to that representation with a few exceptions:
- int64, fixed64, uint64 are written as JSON numbers instead of strings
- `Any` objects don't have any special handling, so the value will be a base64 string, and the type URL field name is `typeUrl` instead of `@type`

### Protobuf 2 Support

Support has been dropped for `com.google.protobuf:protobuf-java:2.x`, but you can use an older release if necessary:
- 0.9.10-jackson2.9-proto2
- 0.9.10-jackson2.8-proto2
- 0.9.10-jackson2.7-proto2
- 0.9.10-preJackson2.7-proto2

### Gotchas

This library assumes that field names in proto files will be lowercase with underscores, as is recommended by the protobuf style guide: https://developers.google.com/protocol-buffers/docs/style#message-and-field-names
> Use underscore_separated_names for field names – for example, song_name.

If your field names don't match this convention, you may need to set a `PropertyNamingStrategy` on your `ObjectMapper` for things to work as expected. For example, if your proto field names are camel case, you could configure your `ObjectMapper` to use `PropertyNamingStrategy.LOWER_CAMEL_CASE`.
