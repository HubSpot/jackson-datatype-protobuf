## Overview

Jackson module that adds support for serializing and deserializing Google's 
[Protocol Buffers](https://code.google.com/p/protobuf/) to and from JSON.

### Instantly Code
[![alt](https://codenvy.com/factory/resources/factory.png)](https://codenvy.com/factory?v=1.0&pname=hubspot-jackson&wname=ecavazos&vcs=git&vcsurl=http%3A%2F%2Fcodenvy.com%2Fgit%2F48%2Fe7%2Fdc%2Fworkspaceu909ru5xicwtsrnx%2Fhubspot-jackson&idcommit=b4b5df915c37a26380aef0775fa5aef91f9b8eee&action=openproject&ptype=Servlet%2FJSP)

## Usage

### Maven dependency

To use module on Maven-based projects, use following dependency:

```xml
<dependency>
  <groupId>com.hubspot.jackson</groupId>
  <artifactId>jackson-datatype-protobuf</artifactId>
  <version>0.4.0</version>
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
