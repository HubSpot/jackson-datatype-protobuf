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

| | Library Version 0.9.5 | Library Version 0.9.4 | Library Version 0.9.3 |
| ----- | --------------------- | --------------------- | --------------------- |
| Jackson 2.3.x | Compatible | Compatible | Compatible |
| Jackson 2.4.x | Compatible | Compatible | Compatible |
| Jackson 2.5.x | Compatible | Compatible | Compatible |
| Jackson 2.6.x | Compatible | Compatible | Compatible |
| Jackson 2.7.x | Compatible | Compatible | Compatible |
| Jackson 2.8.x | Compatible | Compatible | **INCOMPATIBLE** |
| Jackson 2.9.x | Compatible | **INCOMPATIBLE** | **INCOMPATIBLE** |
