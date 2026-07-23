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

    private JwtAuthenticationFilter filter() {
        return new JwtAuthenticationFilter(jwtTokenProvider, userRepository);
    }

    private MockHttpServletRequest requestWithAuthorization(String value) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", value);
        return request;
    }
}
