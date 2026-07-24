package com.taehyun.storyseed.config.jwt;

import com.taehyun.storyseed.user.domain.User;
import com.taehyun.storyseed.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import jakarta.servlet.http.Cookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    private final JwtCookieProvider jwtCookieProvider = new JwtCookieProvider();

    @Mock
    private UserRepository userRepository;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void validBearerTokenStoresAuthentication() throws Exception {
        User user = User.createLocal(
                "user@example.com",
                "encoded-password",
                "taehyun"
        );
        when(jwtTokenProvider.validateToken("valid-token")).thenReturn(true);
        when(jwtTokenProvider.getEmailFromToken("valid-token"))
                .thenReturn("user@example.com");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        filter().doFilter(
                requestWithAuthorization("Bearer valid-token"),
                new MockHttpServletResponse(),
                new MockFilterChain()
        );

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        assertSame(user, authentication.getPrincipal());
        assertEquals("ROLE_USER", authentication.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    void missingAuthorizationHeaderDoesNotAuthenticate() throws Exception {
        filter().doFilter(
                new MockHttpServletRequest(),
                new MockHttpServletResponse(),
                new MockFilterChain()
        );

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(jwtTokenProvider, never()).validateToken(org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    void validCookieTokenStoresAuthentication() throws Exception {
        User user = User.createLocal(
                "user@example.com",
                "encoded-password",
                "taehyun"
        );
        when(jwtTokenProvider.validateToken("cookie-token")).thenReturn(true);
        when(jwtTokenProvider.getEmailFromToken("cookie-token"))
                .thenReturn("user@example.com");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie(
                JwtCookieProvider.ACCESS_TOKEN_COOKIE_NAME,
                "cookie-token"
        ));

        filter().doFilter(
                request,
                new MockHttpServletResponse(),
                new MockFilterChain()
        );

        assertSame(
                user,
                SecurityContextHolder.getContext().getAuthentication().getPrincipal()
        );
    }

    @Test
    void authorizationHeaderTakesPriorityOverCookie() throws Exception {
        MockHttpServletRequest request = requestWithAuthorization("Bearer header-token");
        request.setCookies(new Cookie(
                JwtCookieProvider.ACCESS_TOKEN_COOKIE_NAME,
                "cookie-token"
        ));
        when(jwtTokenProvider.validateToken("header-token")).thenReturn(false);

        filter().doFilter(
                request,
                new MockHttpServletResponse(),
                new MockFilterChain()
        );

        verify(jwtTokenProvider).validateToken("header-token");
        verify(jwtTokenProvider, never()).validateToken("cookie-token");
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void nonBearerAuthorizationDoesNotAuthenticate() throws Exception {
        filter().doFilter(
                requestWithAuthorization("Basic credentials"),
                new MockHttpServletResponse(),
                new MockFilterChain()
        );

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(jwtTokenProvider, never()).validateToken(org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    void invalidTokenDoesNotAuthenticate() throws Exception {
        when(jwtTokenProvider.validateToken("invalid-token")).thenReturn(false);

        filter().doFilter(
                requestWithAuthorization("Bearer invalid-token"),
                new MockHttpServletResponse(),
                new MockFilterChain()
        );

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(userRepository, never()).findByEmail(org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    void invalidCookieTokenDoesNotAuthenticate() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie(
                JwtCookieProvider.ACCESS_TOKEN_COOKIE_NAME,
                "invalid-cookie-token"
        ));
        when(jwtTokenProvider.validateToken("invalid-cookie-token")).thenReturn(false);

        filter().doFilter(
                request,
                new MockHttpServletResponse(),
                new MockFilterChain()
        );

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(userRepository, never()).findByEmail(org.mockito.ArgumentMatchers.anyString());
    }

    private JwtAuthenticationFilter filter() {
        return new JwtAuthenticationFilter(
                jwtTokenProvider,
                jwtCookieProvider,
                userRepository
        );
    }

    private MockHttpServletRequest requestWithAuthorization(String value) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", value);
        return request;
    }
}
