package com.hubspot.jackson.datatype.protobuf;

import static com.hubspot.jackson.datatype.protobuf.util.ObjectMapperHelper.camelCase;
import static com.hubspot.jackson.datatype.protobuf.util.ObjectMapperHelper.underscore;

import org.junit.Test;

import com.hubspot.jackson.datatype.protobuf.util.TestProtobuf.AllFields;
import com.hubspot.jackson.datatype.protobuf.util.TestProtobuf.RepeatedFields;
import com.hubspot.jackson.datatype.protobuf.util.TestProtobuf3.AllFieldsProto3;
import com.hubspot.jackson.datatype.protobuf.util.TestProtobuf3.RepeatedFieldsProto3;
import com.kjetland.jackson.jsonSchema.JsonSchemaGenerator;

public class SchemaTest {

  @Test
  public void itDoesntThrowForAllFieldsProto2CamelCase() {
    JsonSchemaGenerator schemaGenerator = new JsonSchemaGenerator(camelCase());
    schemaGenerator.generateJsonSchema(AllFields.class);
  }

  @Test
  public void itDoesntThrowForAllFieldsProto2Underscore() {
    JsonSchemaGenerator schemaGenerator = new JsonSchemaGenerator(underscore());
    schemaGenerator.generateJsonSchema(AllFields.class);
  }

  @Test
  public void itDoesntThrowForRepeatedFieldsProto2CamelCase() {
    JsonSchemaGenerator schemaGenerator = new JsonSchemaGenerator(camelCase());
    schemaGenerator.generateJsonSchema(RepeatedFields.class);
  }

  @Test
  public void itDoesntThrowForRepeatedFieldsProto2Underscore() {
    JsonSchemaGenerator schemaGenerator = new JsonSchemaGenerator(underscore());
    schemaGenerator.generateJsonSchema(RepeatedFields.class);
  }

  @Test
  public void itDoesntThrowForAllFieldsProto3CamelCase() {
    JsonSchemaGenerator schemaGenerator = new JsonSchemaGenerator(camelCase());
    schemaGenerator.generateJsonSchema(AllFieldsProto3.class);
  }

  @Test
  public void itDoesntThrowForAllFieldsProto3Underscore() {
    JsonSchemaGenerator schemaGenerator = new JsonSchemaGenerator(underscore());
    schemaGenerator.generateJsonSchema(AllFieldsProto3.class);
  }

  @Test
  public void itDoesntThrowForRepeatedFieldsProto3CamelCase() {
    JsonSchemaGenerator schemaGenerator = new JsonSchemaGenerator(camelCase());
    schemaGenerator.generateJsonSchema(RepeatedFieldsProto3.class);
  }

  @Test
  public void itDoesntThrowForRepeatedFieldsProto3Underscore() {
    JsonSchemaGenerator schemaGenerator = new JsonSchemaGenerator(underscore());
    schemaGenerator.generateJsonSchema(RepeatedFieldsProto3.class);
  }
}
