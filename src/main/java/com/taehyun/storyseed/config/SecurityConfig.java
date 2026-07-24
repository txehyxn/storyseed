package com.taehyun.storyseed.config;

import com.taehyun.storyseed.config.jwt.JwtAuthenticationFilter;
import com.taehyun.storyseed.config.jwt.JwtCookieProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private static final String SIGN_UP_PATH = "/api/users/signup";
    private static final String LOGIN_PATH = "/api/users/login";

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationFilter jwtAuthenticationFilter,
            JwtCookieProvider jwtCookieProvider,
            AuthenticationEntryPoint authenticationEntryPoint
    ) throws Exception {
        http
                .csrf(csrf -> csrf.ignoringRequestMatchers(SIGN_UP_PATH, LOGIN_PATH))
                .exceptionHandling(exceptions ->
                        exceptions.authenticationEntryPoint(authenticationEntryPoint)
                )
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.POST, SIGN_UP_PATH, LOGIN_PATH).permitAll()
                        .requestMatchers(
                                "/",
                                "/signup",
                                "/login",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/favicon.ico"
                        ).permitAll()
                        .requestMatchers("/stories/**").authenticated()
                        .anyRequest().authenticated()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .addLogoutHandler((request, response, authentication) ->
                                response.addHeader(
                                        HttpHeaders.SET_COOKIE,
                                        jwtCookieProvider.deleteAccessTokenCookie(
                                                request.isSecure()
                                        ).toString()
                                )
                        )
                        .logoutSuccessUrl("/login")
                )
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}
