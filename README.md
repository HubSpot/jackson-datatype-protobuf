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
  <version>0.9.5</version>
</dependency>
```

(or whatever version is most up-to-date at the moment)

### Registering module

Registration is done as follows:

```java
ObjectMapper mapper = new ObjectMapper();
mapper.registerModule(new ProtobufModule());
```

after which functionality is available for all normal Jackson operations.

### Jackson Compatibility

See the following compatibility matrix:

| Library Version | Jackson Version | Compatible |
| --------------- | --------------- | ---------- |
| 0.9.3 | 2.3.x | Yes |
| 0.9.3 | 2.4.x | Yes |
| 0.9.3 | 2.5.x | Yes |
| 0.9.3 | 2.6.x | Yes |
| 0.9.3 | 2.7.x | Yes |
| 0.9.3 | 2.8.x | **NO** |
| 0.9.3 | 2.9.x | **NO** |
| 0.9.4 | 2.3.x | Yes |
| 0.9.4 | 2.4.x | Yes |
| 0.9.4 | 2.5.x | Yes |
| 0.9.4 | 2.6.x | Yes |
| 0.9.4 | 2.7.x | Yes |
| 0.9.4 | 2.8.x | Yes |
| 0.9.4 | 2.9.x | **NO** |
| 0.9.5 | 2.3.x | Yes |
| 0.9.5 | 2.4.x | Yes |
| 0.9.5 | 2.5.x | Yes |
| 0.9.5 | 2.6.x | Yes |
| 0.9.5 | 2.7.x | Yes |
| 0.9.5 | 2.8.x | Yes |
| 0.9.5 | 2.9.x | Yes |
