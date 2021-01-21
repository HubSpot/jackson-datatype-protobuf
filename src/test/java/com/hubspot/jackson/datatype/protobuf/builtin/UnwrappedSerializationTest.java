package com.hubspot.jackson.datatype.protobuf.builtin;

import static com.hubspot.jackson.datatype.protobuf.util.ObjectMapperHelper.camelCase;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.google.protobuf.ListValue;
import com.google.protobuf.NullValue;
import com.google.protobuf.Value;
import com.hubspot.jackson.datatype.protobuf.util.BuiltInProtobufs;

public class UnwrappedSerializationTest {
    @Test
    public void itWritesUnwrappedNullValue() throws IOException {
        BuiltInProtobufs.HasValue message = BuiltInProtobufs.HasValue
                .newBuilder()
                .setValue(Value.newBuilder().setNullValue(NullValue.NULL_VALUE).build())
                .build();
        ValueBean bean = new ValueBean();
        bean.setHasValue(message);
        String json = camelCase().writeValueAsString(bean);
        assertThat(json).isEqualTo("{\"value\":null}");
    }

    @Test
    public void itWritesUnwrappedListValue() throws IOException {
        ListValue list = ListValue.newBuilder().addValues(Value.newBuilder().setStringValue("test").build()).build();
        BuiltInProtobufs.HasValue message = BuiltInProtobufs.HasValue
                .newBuilder()
                .setValue(Value.newBuilder().setListValue(list).build())
                .build();
        ValueBean bean = new ValueBean();
        bean.setHasValue(message);
        String json = camelCase().writeValueAsString(bean);
        assertThat(json).isEqualTo("{\"value\":[\"test\"]}");
    }

    public static class ValueBean {
        @JsonUnwrapped
        private BuiltInProtobufs.HasValue hasValue;

        public BuiltInProtobufs.HasValue getHasValue() {
            return hasValue;
        }
        public void setHasValue(BuiltInProtobufs.HasValue hasValue) {
            this.hasValue = hasValue;
        }
    }
}
