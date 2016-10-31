package com.hubspot.jackson.datatype.protobuf;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.introspect.POJOPropertiesCollector;
import com.fasterxml.jackson.databind.introspect.POJOPropertyBuilder;
import com.google.common.base.CaseFormat;
import com.google.protobuf.Descriptors;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import static java.util.Locale.ENGLISH;

public class ProtobufPropertiesCollector extends POJOPropertiesCollector {
    protected ProtobufPropertiesCollector(MapperConfig<?> config, boolean forSerialization, JavaType type, AnnotatedClass classDef, String mutatorPrefix) {
        super(config, forSerialization, type, classDef, mutatorPrefix);
    }

    @Override
    protected void _addFields(Map<String, POJOPropertyBuilder> props) {
        Class clazz = _type.getRawClass();
        Descriptors.Descriptor descriptor;

        try {
            descriptor = (Descriptors.Descriptor) clazz.getDeclaredMethod("getDescriptor").invoke(null);
        } catch (Exception e) {
            throw new RuntimeException("Unable to invoke getDescriptor() for type " + clazz, e);
        }

        for (Descriptors.FieldDescriptor field : descriptor.getFields()) {
            addProperty(props, field, clazz);
        }
    }

    private String computeFieldGetter(String fieldName, Descriptors.FieldDescriptor field) {
        if (field.isRepeated()) {
            if (field.isMapField()) {
                return "get" + capitalize(fieldName) + "Map";
            }

            return "get" + capitalize(fieldName) + "List";
        }

        return "get" + capitalize(fieldName);
    }

    private String computeFieldSetter(String fieldName, Descriptors.FieldDescriptor field) {
        if (field.isRepeated()) {
            if (field.isMapField()) {
                return "putAll" + capitalize(fieldName);
            }

            return "addAll" + capitalize(fieldName);
        }

        return "set" + capitalize(fieldName);
    }

    /**
     *
     * @param props
     * @param field
     * @param clazz class to introspect
     */
    protected void addProperty(Map<String, POJOPropertyBuilder> props, Descriptors.FieldDescriptor field, Class clazz) {
        // Make sure to convert field name from official protobuf snake case to camel case
        String fieldName = field.getName();

        String fieldGetter = computeFieldGetter(fieldName, field);
        String fieldSetter = computeFieldSetter(fieldName, field);

        try {
            clazz.getMethod(fieldGetter);
        } catch (NoSuchMethodException e) {
            fieldName = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, field.getName());
            fieldGetter = computeFieldGetter(fieldName, field);
            fieldSetter = computeFieldSetter(fieldName, field);
        }

        Method getterMethod = null, setterMethod = null;

        try {
            getterMethod = clazz.getMethod(fieldGetter);
            setterMethod = clazz.getMethod(fieldSetter);
        } catch (NoSuchMethodException e) {
            // Ignore
        }

        PropertyName pn = new PropertyName(fieldName);

        if (getterMethod != null) {
            _property(props, pn).addGetter(
                    new AnnotatedMethod(_classDef, getterMethod, null, null),
                    pn,
                    true, true, false);
        }

        if (setterMethod != null) {
            _property(props, pn).addSetter(
                    new AnnotatedMethod(_classDef, setterMethod, null, null),
                    pn,
                    true, true, false);
        }
    }

    /**
     * Returns a String which capitalizes the first letter of the string.
     */
    public static String capitalize(String name) {
        if (name == null || name.length() == 0) {
            return name;
        }
        return name.substring(0, 1).toUpperCase(ENGLISH) + name.substring(1);
    }

    @Override
    protected void _addCreators(Map<String, POJOPropertyBuilder> props) {
    }

    @Override
    protected void _addMethods(Map<String, POJOPropertyBuilder> props) {
    }

    @Override
    protected void _addInjectables(Map<String, POJOPropertyBuilder> props) {
    }

    @Override
    public List<BeanPropertyDefinition> getProperties() {
        return super.getProperties();
    }
}
