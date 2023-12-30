package com.hubspot.jackson.datatype.protobuf;

import static com.hubspot.jackson.datatype.protobuf.util.ObjectMapperHelper.camelCase;
import static com.hubspot.jackson.datatype.protobuf.util.ObjectMapperHelper.underscore;

import com.hubspot.jackson.datatype.protobuf.util.BuiltInProtobufs.HasAllMapKeys;
import com.hubspot.jackson.datatype.protobuf.util.BuiltInProtobufs.HasAllMapValues;
import com.hubspot.jackson.datatype.protobuf.util.BuiltInProtobufs.HasAny;
import com.hubspot.jackson.datatype.protobuf.util.BuiltInProtobufs.HasDuration;
import com.hubspot.jackson.datatype.protobuf.util.BuiltInProtobufs.HasFieldMask;
import com.hubspot.jackson.datatype.protobuf.util.BuiltInProtobufs.HasListValue;
import com.hubspot.jackson.datatype.protobuf.util.BuiltInProtobufs.HasNullValue;
import com.hubspot.jackson.datatype.protobuf.util.BuiltInProtobufs.HasOneof;
import com.hubspot.jackson.datatype.protobuf.util.BuiltInProtobufs.HasStruct;
import com.hubspot.jackson.datatype.protobuf.util.BuiltInProtobufs.HasTimestamp;
import com.hubspot.jackson.datatype.protobuf.util.BuiltInProtobufs.HasValue;
import com.hubspot.jackson.datatype.protobuf.util.BuiltInProtobufs.HasWrappedPrimitives;
import com.hubspot.jackson.datatype.protobuf.util.TestProtobuf.AllFields;
import com.hubspot.jackson.datatype.protobuf.util.TestProtobuf.RepeatedFields;
import com.hubspot.jackson.datatype.protobuf.util.TestProtobuf3.AllFieldsProto3;
import com.hubspot.jackson.datatype.protobuf.util.TestProtobuf3.RepeatedFieldsProto3;
import com.kjetland.jackson.jsonSchema.JsonSchemaGenerator;
import org.junit.Test;

public class SchemaTest {

  @Test
  public void itHandlesProto2CamelCase() {
    JsonSchemaGenerator schemaGenerator = new JsonSchemaGenerator(camelCase());
    schemaGenerator.generateJsonSchema(AllFields.class);
    schemaGenerator.generateJsonSchema(RepeatedFields.class);
  }

  @Test
  public void itHandlesProto2Underscore() {
    JsonSchemaGenerator schemaGenerator = new JsonSchemaGenerator(underscore());
    schemaGenerator.generateJsonSchema(AllFields.class);
    schemaGenerator.generateJsonSchema(RepeatedFields.class);
  }

  @Test
  public void itHandlesProto3CamelCase() {
    JsonSchemaGenerator schemaGenerator = new JsonSchemaGenerator(camelCase());
    schemaGenerator.generateJsonSchema(AllFieldsProto3.class);
    schemaGenerator.generateJsonSchema(RepeatedFieldsProto3.class);
    schemaGenerator.generateJsonSchema(HasAny.class);
    schemaGenerator.generateJsonSchema(HasDuration.class);
    schemaGenerator.generateJsonSchema(HasFieldMask.class);
    schemaGenerator.generateJsonSchema(HasListValue.class);
    schemaGenerator.generateJsonSchema(HasNullValue.class);
    schemaGenerator.generateJsonSchema(HasStruct.class);
    schemaGenerator.generateJsonSchema(HasTimestamp.class);
    schemaGenerator.generateJsonSchema(HasValue.class);
    schemaGenerator.generateJsonSchema(HasWrappedPrimitives.class);
    schemaGenerator.generateJsonSchema(HasOneof.class);
    schemaGenerator.generateJsonSchema(HasAllMapKeys.class);
    schemaGenerator.generateJsonSchema(HasAllMapValues.class);
  }

  @Test
  public void itHandlesProto3Underscore() {
    JsonSchemaGenerator schemaGenerator = new JsonSchemaGenerator(underscore());
    schemaGenerator.generateJsonSchema(AllFieldsProto3.class);
    schemaGenerator.generateJsonSchema(RepeatedFieldsProto3.class);
    schemaGenerator.generateJsonSchema(HasAny.class);
    schemaGenerator.generateJsonSchema(HasDuration.class);
    schemaGenerator.generateJsonSchema(HasFieldMask.class);
    schemaGenerator.generateJsonSchema(HasListValue.class);
    schemaGenerator.generateJsonSchema(HasNullValue.class);
    schemaGenerator.generateJsonSchema(HasStruct.class);
    schemaGenerator.generateJsonSchema(HasTimestamp.class);
    schemaGenerator.generateJsonSchema(HasValue.class);
    schemaGenerator.generateJsonSchema(HasWrappedPrimitives.class);
    schemaGenerator.generateJsonSchema(HasOneof.class);
    schemaGenerator.generateJsonSchema(HasAllMapKeys.class);
    schemaGenerator.generateJsonSchema(HasAllMapValues.class);
  }
}
