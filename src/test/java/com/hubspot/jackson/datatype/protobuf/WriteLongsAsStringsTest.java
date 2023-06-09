package com.hubspot.jackson.datatype.protobuf;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hubspot.jackson.datatype.protobuf.util.TestProtobuf.AllFields;

public class WriteLongsAsStringsTest {

  @Test
  public void itDoesntWriteLongsAsStringsByDefault() throws IOException {
    ProtobufJacksonConfig config = ProtobufJacksonConfig.builder().build();

    assertThat(writeSomeInt64s(config)).isEqualTo("{\"int64\":-42,\"uint64\":42}");
  }

  @Test
  public void itDoesntWriteLongsAsStringsWhenDisabled() throws IOException {
    ProtobufJacksonConfig config = ProtobufJacksonConfig.builder().writeLongsAsStrings(false).build();

    assertThat(writeSomeInt64s(config)).isEqualTo("{\"int64\":-42,\"uint64\":42}");
  }

  @Test
  public void itDoesntWriteLongsAsStringsWhenEnabledThenDisabled() throws IOException {
    ProtobufJacksonConfig config = ProtobufJacksonConfig.builder()
        .writeLongsAsStrings(true)
        .writeLongsAsStrings(false)
        .build();

    assertThat(writeSomeInt64s(config)).isEqualTo("{\"int64\":-42,\"uint64\":42}");
  }

  @Test
  public void itWritesLongsAsStringsWhenEnabled() throws IOException {
    ProtobufJacksonConfig config = ProtobufJacksonConfig.builder().writeLongsAsStrings(true).build();

    assertThat(writeSomeInt64s(config)).isEqualTo("{\"int64\":\"-42\",\"uint64\":\"42\"}");
  }

  @Test
  public void itWritesLongsAsStringsWhenDisabledThenEnabled() throws IOException {
    ProtobufJacksonConfig config = ProtobufJacksonConfig.builder()
        .writeLongsAsStrings(false)
        .writeLongsAsStrings(true)
        .build();

    assertThat(writeSomeInt64s(config)).isEqualTo("{\"int64\":\"-42\",\"uint64\":\"42\"}");
  }

  private static String writeSomeInt64s(final ProtobufJacksonConfig config) throws IOException {
    ObjectMapper mapper = new ObjectMapper().registerModules(new ProtobufModule(config));
    AllFields someInt64s = AllFields.newBuilder().setInt64(-42).setUint64(42).build();
    return mapper.writeValueAsString(someInt64s);
  }
}
