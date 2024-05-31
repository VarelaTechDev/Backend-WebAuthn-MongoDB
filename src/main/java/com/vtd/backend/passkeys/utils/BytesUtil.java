package com.vtd.backend.passkeys.utils;

import org.bson.types.ObjectId;

public class BytesUtil {
  public static byte[] stringToBytes(String objectIdStr) {
    ObjectId objectId = new ObjectId(objectIdStr);
    return objectId.toByteArray();
  }

  public static String bytesToString(byte[] bytes) {
    ObjectId objectId = new ObjectId(bytes);
    return objectId.toHexString();
  }
}
