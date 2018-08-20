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

There are separate versions based on which version of Jackson you are using, as well as which version of protobuf (proto2 vs. proto3). The follow table shows which version to use based on these criteria:

| | Jackson <= 2.6.x | Jackson 2.7.x | Jackson 2.8.x | Jackson 2.9.x |
| ----- | ---------- | ------------- | ------------- | ------------- |
| Protobuf 2.x | 0.9.10-preJackson2.7-proto2 | 0.9.10-jackson2.7-proto2 | 0.9.10-jackson2.8-proto2 | 0.9.10-jackson2.9-proto2 |
| Protobuf 3.x | 0.9.10-preJackson2.7-proto3 | 0.9.10-jackson2.7-proto3 | 0.9.10-jackson2.8-proto3 | 0.9.10-jackson2.9-proto3 |

### Protobuf 3 Support

As noted above, if you are using Protobuf3 there is a separate version of the library to use. It has support for all of the Protobuf 3 features and built-in types. The JSON representation should match the encoding specified [here](https://developers.google.com/protocol-buffers/docs/proto3#json), with a few exceptions:
- int64, fixed64, uint64 are written as JSON numbers instead of strings
- `Any` objects don't have any special handling, so the value will be a base64 string, and the type URL field name is `typeUrl` instead of `@type`

Compared to Protobuf2 behavior, the main difference is that field presence is only supported for `Message` fields. This means we can't tell if a primitive field was explicitly set to its default value, or not set at all. So for Protobuf3 messages, the library will write primitive fields that are set to default values. If you want to disable this, you can set your JSON inclusion to `Include.NON_DEFAULT`. Currently you need to set this inclusion globally, but we could make it more granular in the future.

### Registering module

Registration is done as follows:

```java
ObjectMapper mapper = new ObjectMapper();
mapper.registerModule(new ProtobufModule());
```

after which functionality is available for all normal Jackson operations.
