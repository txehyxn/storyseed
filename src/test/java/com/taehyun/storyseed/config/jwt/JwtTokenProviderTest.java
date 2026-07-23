package com.taehyun.storyseed.config.jwt;

import com.taehyun.storyseed.user.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtTokenProviderTest {

    private static final String SECRET =
            "storyseed-test-secret-key-long-enough-for-hs256";
    private static final long EXPIRATION_SECONDS = 3600L;

    @Test
    void createAccessTokenIncludesExpectedClaimsAndValidSignature() {
        JwtTokenProvider provider = new JwtTokenProvider(properties(SECRET, EXPIRATION_SECONDS));
        User user = User.createLocal(
                "user@example.com",
                "encoded-password",
                "taehyun"
        );
        ReflectionTestUtils.setField(user, "id", 1L);

        String token = provider.createAccessToken(user);

        assertFalse(token.isBlank());

        Claims claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertEquals("user@example.com", claims.getSubject());
        assertEquals(1L, claims.get("id", Number.class).longValue());
        assertEquals("user@example.com", claims.get("email", String.class));
        assertEquals("USER", claims.get("role", String.class));
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
        assertEquals(
                EXPIRATION_SECONDS,
                (claims.getExpiration().getTime() - claims.getIssuedAt().getTime()) / 1000
        );
        assertEquals(EXPIRATION_SECONDS, provider.getAccessTokenExpirationSeconds());
    }

    @Test
    void rejectsSecretShorterThanHs256Minimum() {
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> new JwtTokenProvider(properties("short-secret", EXPIRATION_SECONDS))
        );

        assertEquals("JWT secret must be at least 32 bytes for HS256", exception.getMessage());
    }

    private JwtProperties properties(String secret, long expirationSeconds) {
        JwtProperties properties = new JwtProperties();
        properties.setSecret(secret);
        properties.setAccessTokenExpirationSeconds(expirationSeconds);
        return properties;
    }
}
