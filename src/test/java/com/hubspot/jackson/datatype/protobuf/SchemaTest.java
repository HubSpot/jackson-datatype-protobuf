package com.hubspot.jackson.datatype.protobuf;

import static com.hubspot.jackson.datatype.protobuf.util.ObjectMapperHelper.camelCase;

import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.hubspot.jackson.datatype.protobuf.util.TestProtobuf.AllFields;
import com.kjetland.jackson.jsonSchema.JsonSchemaGenerator;

public class SchemaTest {

  @Test
  public void testAllFieldsCamelCase() throws IOException {
    JsonSchemaGenerator schemaGenerator = new JsonSchemaGenerator(camelCase());
    JsonNode schema = schemaGenerator.generateJsonSchema(AllFields.class);

    System.out.println(camelCase().writerWithDefaultPrettyPrinter().writeValueAsString(schema));
  }
}
