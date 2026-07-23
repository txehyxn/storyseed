package com.taehyun.storyseed.config.jwt;

import com.taehyun.storyseed.user.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private static final int HS256_MINIMUM_KEY_BYTES = 32;

    private final SecretKey secretKey;
    private final long accessTokenExpirationSeconds;

    public JwtTokenProvider(JwtProperties properties) {
        byte[] secretBytes = properties.getSecret().getBytes(StandardCharsets.UTF_8);
        if (secretBytes.length < HS256_MINIMUM_KEY_BYTES) {
            throw new IllegalStateException("JWT secret must be at least 32 bytes for HS256");
        }

        this.secretKey = Keys.hmacShaKeyFor(secretBytes);
        this.accessTokenExpirationSeconds = properties.getAccessTokenExpirationSeconds();
    }

    public String createAccessToken(User user) {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plusSeconds(accessTokenExpirationSeconds);

        return Jwts.builder()
                .subject(user.getEmail())
                .claim("id", user.getId())
                .claim("email", user.getEmail())
                .claim("role", user.getRole().name())
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(expiresAt))
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    public long getAccessTokenExpirationSeconds() {
        return accessTokenExpirationSeconds;
    }

    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException exception) {
            return false;
        }
    }

    public String getEmailFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
