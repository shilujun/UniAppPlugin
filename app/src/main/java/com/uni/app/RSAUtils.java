package com.uni.app;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.util.Base64;
import java.security.KeyFactory;
import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

public class RSAUtils {

  @RequiresApi(api = Build.VERSION_CODES.O)
  public static String encryptRSAToString(String text, String strPublicKey) {

    byte[] cipherText = null;
    String strEncryInfoData="";
    try {

      KeyFactory keyFac = KeyFactory.getInstance("RSA");

      KeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(strPublicKey.trim().getBytes()));
      PublicKey pubKey = keyFac.generatePublic(keySpec);

      // get an RSA cipher object and print the provider
      final Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
      // encrypt the plain text using the public key
      cipher.init(Cipher.ENCRYPT_MODE, pubKey);
      cipherText = cipher.doFinal(text.getBytes(StandardCharsets.UTF_8));

      strEncryInfoData = Base64.getEncoder().encodeToString(cipherText);

    } catch (Exception e) {
      e.printStackTrace();
    }
    return strEncryInfoData.replaceAll("(\\r|\\n)", "");
  }
}
