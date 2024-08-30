package com.lucky.around.meal.datapipeline;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtil {
  public static String generateSHA256Hash(String data) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hashBytes = digest.digest(data.getBytes());

      StringBuilder hexString = new StringBuilder();
      for (byte b : hashBytes) {
        String hex = Integer.toHexString(0xff & b);
        if (hex.length() == 1) {
          hexString.append('0');
        }
        hexString.append(hex);
      }

      return hexString.toString();
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("Error generating hash", e);
    }
  }
}
