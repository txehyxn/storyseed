package com.taehyun.storyseed.config.jwt;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;

@Component
public class JwtCookieProvider {

    public static final String ACCESS_TOKEN_COOKIE_NAME = "access_token";

    public ResponseCookie createAccessTokenCookie(
            String token,
            long expirationSeconds,
            boolean secure
    ) {
        return baseCookie(token, secure)
                .maxAge(Duration.ofSeconds(expirationSeconds))
                .build();
    }

    public ResponseCookie deleteAccessTokenCookie(boolean secure) {
        return baseCookie("", secure)
                .maxAge(Duration.ZERO)
                .build();
    }

    public Optional<String> resolveAccessToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return Optional.empty();
        }

        return Arrays.stream(cookies)
                .filter(cookie -> ACCESS_TOKEN_COOKIE_NAME.equals(cookie.getName()))
                .map(Cookie::getValue)
                .filter(value -> !value.isBlank())
                .findFirst();
    }

    private ResponseCookie.ResponseCookieBuilder baseCookie(String value, boolean secure) {
        return ResponseCookie.from(ACCESS_TOKEN_COOKIE_NAME, value)
                .httpOnly(true)
                .secure(secure)
                .path("/")
                .sameSite("Lax");
    }
}
