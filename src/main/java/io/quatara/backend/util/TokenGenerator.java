package io.quatara.backend.util;

import java.security.SecureRandom;
import java.util.Base64;

public class TokenGenerator {

    // SecureRandom is cryptographically strong
    private static final SecureRandom secureRandom = new SecureRandom();

    // URL-safe Base64 encoder without padding
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder().withoutPadding();

    public static String generateNewToken() {
        byte[] randomBytes = new byte[16]; // 128 bits of entropy
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
        // Generates an unguessable string like: k9X2_mPq8RzL4w
    }
}
