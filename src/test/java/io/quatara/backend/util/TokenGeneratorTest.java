package io.quatara.backend.util;

import org.junit.jupiter.api.Test;
import java.util.HashSet;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

public class TokenGeneratorTest {

    @Test
    public void generateNewToken_ShouldBeUniqueAndUrlSafe() {
        Set<String> tokens = new HashSet<>();
        int iterations = 100;
        for (int i = 0; i < iterations; i++) {
            String token = TokenGenerator.generateNewToken();
            // Token should be URL-safe Base64 without padding, length 22 for 16 bytes input
            assertEquals(22, token.length(), "Token length should be 22 characters");
            assertTrue(token.matches("[A-Za-z0-9_-]+"), "Token contains only URL-safe Base64 characters");
            assertTrue(tokens.add(token), "Token should be unique across generations");
        }
        assertEquals(iterations, tokens.size(), "All generated tokens should be unique");
    }
}
