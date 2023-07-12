package com.hubspot.jackson.datatype.protobuf.internal;

import java.math.BigInteger;

public class Constants {
  public static final long MIN_UINT32 = Integer.MIN_VALUE;
  public static final long MAX_UINT32 = 0xFFFFFFFFL;
  public static final BigInteger MIN_UINT64 = BigInteger.valueOf(Long.MIN_VALUE);
  public static final BigInteger MAX_UINT64 = new BigInteger("FFFFFFFFFFFFFFFF", 16);
}
