package com.planetway.relyingpartyapp.util;

import java.security.SecureRandom;
import java.util.Base64;

public abstract class SecureRandomUtil {

    public static String randomBase64(int byteLength) {
        SecureRandom secureRandom = new SecureRandom();

        byte[] buffer = new byte[byteLength];
        secureRandom.nextBytes(buffer);

        return Base64.getUrlEncoder().encodeToString(buffer);
        
    }
    
    public static String randomString(int byteLength) {
        SecureRandom secureRandom = new SecureRandom();

        byte[] buffer = new byte[byteLength];
        secureRandom.nextBytes(buffer);

        return buffer.toString();
        
    }
}
