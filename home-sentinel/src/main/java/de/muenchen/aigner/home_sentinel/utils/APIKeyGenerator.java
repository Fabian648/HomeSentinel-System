package de.muenchen.aigner.home_sentinel.utils;

import java.security.SecureRandom;
import java.util.Base64;

public class APIKeyGenerator {
    public static String generateAPIKey() {
        SecureRandom random = new SecureRandom();
        byte[] key = new byte[32];
        random.nextBytes(key);
        return Base64.getEncoder().encodeToString(key);
    }
}
