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

There are separate versions based on which version of Jackson you are using, as well as which version of protobuf (protobuf 3 compatibility is in the works). The follow table shows which version to use based on these factors:

| | Jackson <= 2.6.x | Jackson 2.7.x | Jackson 2.8.x | Jackson 2.9.x |
| ----- | ---------- | ------------- | ------------- | ------------- |
| Protobuf 2.x | 0.9.7-preJackson2.7-proto2 | 0.9.7-jackson2.7-proto2 | 0.9.7-jackson2.8-proto2 | 0.9.7-jackson2.9-proto2 |
| Protobuf 3.x | coming soon | coming soon | coming soon | coming soon |

### Registering module

Registration is done as follows:

```java
ObjectMapper mapper = new ObjectMapper();
mapper.registerModule(new ProtobufModule());
```

after which functionality is available for all normal Jackson operations.
