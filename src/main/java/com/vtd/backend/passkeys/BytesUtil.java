package com.vtd.backend.passkeys;

import java.nio.ByteBuffer;

public class BytesUtil {

  public static byte[] longToBytes(Long longValue) {
    return ByteBuffer.allocate(Long.BYTES).putLong(longValue).array();
  }

  public static long bytesToLong(byte[] bytes) {
    return ByteBuffer.wrap(bytes).getLong();
  }
}
