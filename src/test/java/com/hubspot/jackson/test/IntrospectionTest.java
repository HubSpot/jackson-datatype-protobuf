package com.hubspot.jackson.test;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.hubspot.jackson.test.util.TestIntrospection;
import com.hubspot.jackson.test.util.TestProtobuf;
import org.junit.Test;
import com.hubspot.jackson.test.util.ObjectMapperHelper;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class IntrospectionTest {
    @Test
    public void testIntrospection() {
        TestIntrospection.AllFields c = TestIntrospection.AllFields.getDefaultInstance();
        BeanDescription desc = ObjectMapperHelper.camelCase().getSerializationConfig().introspect(TypeFactory.defaultInstance()
                .constructType(c.getClass()));
        assertEquals(Arrays.asList(
                        "double",
                        "string",
                        "schlumpfen",
                        "enum",
                        "nested",
                        "bar",
                        "camelCaseField",
                        "snakeCaseField"),
                desc.findProperties().stream().map(p -> p.getName()).collect(Collectors.toList()));
        BeanPropertyDefinition nested = desc.findProperties().stream().filter(p -> p.getName().equals("nested")).findFirst().get();
        assertEquals(TestIntrospection.Nested.class, nested.getGetter().getRawReturnType());
    }
}
