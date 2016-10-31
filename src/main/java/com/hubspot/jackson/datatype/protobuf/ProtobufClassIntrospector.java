package com.hubspot.jackson.datatype.protobuf;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.BasicClassIntrospector;
import com.fasterxml.jackson.databind.introspect.POJOPropertiesCollector;
import com.google.protobuf.Message;
import com.google.protobuf.MessageOrBuilder;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressWarnings("serial")
public class ProtobufClassIntrospector extends BasicClassIntrospector {
    @Override
    protected POJOPropertiesCollector constructPropertyCollector(MapperConfig<?> config,
                                                                 AnnotatedClass ac, JavaType type, boolean forSerialization, String mutatorPrefix)
    {
        if (Message.class.isAssignableFrom(type.getRawClass()) ||
                Message.Builder.class.isAssignableFrom(type.getRawClass())) {
            return new ProtobufPropertiesCollector(config, forSerialization, type, ac, mutatorPrefix);
        }

        return super.constructPropertyCollector(config, ac, type, forSerialization, mutatorPrefix);
    }
}
