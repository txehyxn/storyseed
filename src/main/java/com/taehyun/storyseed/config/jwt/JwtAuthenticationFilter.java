package com.taehyun.storyseed.config.jwt;

import com.taehyun.storyseed.user.domain.User;
import com.taehyun.storyseed.user.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtCookieProvider jwtCookieProvider;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(
            JwtTokenProvider jwtTokenProvider,
            JwtCookieProvider jwtCookieProvider,
            UserRepository userRepository
    ) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.jwtCookieProvider = jwtCookieProvider;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            resolveToken(request).ifPresent(this::authenticate);
        }

        filterChain.doFilter(request, response);
    }

    private Optional<String> resolveToken(HttpServletRequest request) {
        String authorization = request.getHeader(AUTHORIZATION_HEADER);
        if (authorization != null) {
            if (!authorization.startsWith(BEARER_PREFIX)) {
                return Optional.empty();
            }

            String token = authorization.substring(BEARER_PREFIX.length());
            return token.isBlank() ? Optional.empty() : Optional.of(token);
        }

        return jwtCookieProvider.resolveAccessToken(request);
    }

    private void authenticate(String token) {
        try {
            if (!jwtTokenProvider.validateToken(token)) {
                return;
            }

            String email = jwtTokenProvider.getEmailFromToken(token);
            userRepository.findByEmail(email).ifPresent(this::setAuthentication);
        } catch (RuntimeException exception) {
            SecurityContextHolder.clearContext();
        }
    }

    private void setAuthentication(User user) {
        SimpleGrantedAuthority authority =
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name());
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(user, null, List.of(authority));

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
