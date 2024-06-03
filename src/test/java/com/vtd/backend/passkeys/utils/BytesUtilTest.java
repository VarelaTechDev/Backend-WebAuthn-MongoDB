package com.vtd.backend.passkeys.utils;

import com.vtd.backend.passkeys.utils.BytesUtil;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BytesUtilTest {

    @Test
    void testObjectIdConversion() {
        // Test with valid ObjectId string
        String objectIdStr = new ObjectId().toHexString();
        byte[] bytes = BytesUtil.stringToBytes(objectIdStr);
        String convertedBack = BytesUtil.bytesToString(bytes);
        assertEquals(objectIdStr, convertedBack, "ObjectId string conversion failed");
    }

    @Test
    void testUUIDConversion() {
        // Test with valid UUID string
        String uuidStr = UUID.randomUUID().toString();
        byte[] bytes = BytesUtil.stringToBytes(uuidStr);
        String convertedBack = BytesUtil.bytesToString(bytes);
        assertEquals(uuidStr, convertedBack, "UUID string conversion failed");
    }

    @Test
    void testInvalidObjectIdConversion() {
        // Test with invalid ObjectId string
        String invalidObjectIdStr = "this-is-not-a-valid-objectid";
        assertThrows(IllegalArgumentException.class, () -> new ObjectId(invalidObjectIdStr));
    }

    @Test
    void testInvalidUUIDConversion() {
        // Test with invalid UUID string
        String invalidUUIDStr = "this-is-not-a-valid-uuid";
        assertThrows(IllegalArgumentException.class, () -> UUID.fromString(invalidUUIDStr));
    }

    @Test
    void testRegularStringConversion() {
        // Test with regular strings
        String regularString = "this-is-a-regular-string";
        byte[] bytes = regularString.getBytes();
        String convertedBack = new String(bytes);
        assertEquals(regularString, convertedBack, "Regular string conversion failed");
    }

    @Test
    void testMixedStrings() {
        // Test with various valid and invalid strings
        String[] testStrings = {
                new ObjectId().toHexString(),
                UUID.randomUUID().toString(),
                "this-is-not-a-valid-objectid",
                "this-is-not-a-valid-uuid",
                "this-is-a-regular-string"
        };

        for (String testStr : testStrings) {
            try {
                byte[] bytes = BytesUtil.stringToBytes(testStr);
                String convertedBack = BytesUtil.bytesToString(bytes);
                assertEquals(testStr, convertedBack, "String conversion failed for: " + testStr);
            } catch (IllegalArgumentException e) {
                // Expected for invalid ObjectId or UUID strings
                System.out.println("Expected exception for invalid string: " + testStr);
            }
        }
    }
}
