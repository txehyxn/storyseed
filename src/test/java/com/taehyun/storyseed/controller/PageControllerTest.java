package com.taehyun.storyseed.controller;

import com.taehyun.storyseed.config.jwt.JwtCookieProvider;
import com.taehyun.storyseed.config.jwt.JwtTokenProvider;
import com.taehyun.storyseed.user.domain.User;
import com.taehyun.storyseed.user.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@ActiveProfiles("test")
class PageControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    void signupPageIsPublic() throws Exception {
        mockMvc.perform(get("/signup"))
                .andExpect(status().isOk())
                .andExpect(view().name("signup"));
    }

    @Test
    void loginPageIsPublic() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    void homePageRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/home"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void homePageAcceptsAuthenticationCookie() throws Exception {
        User user = userRepository.save(User.createLocal(
                "user@example.com",
                "encoded-password",
                "storyteller"
        ));

        mockMvc.perform(get("/home").cookie(accessTokenCookie(user)))
                .andExpect(status().isOk())
                .andExpect(view().name("home"));
    }

    @Test
    void logoutDeletesAuthenticationCookieAndRedirectsToLogin() throws Exception {
        User user = userRepository.save(User.createLocal(
                "user@example.com",
                "encoded-password",
                "storyteller"
        ));

        mockMvc.perform(post("/logout")
                        .cookie(accessTokenCookie(user))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andExpect(cookie().maxAge(
                        JwtCookieProvider.ACCESS_TOKEN_COOKIE_NAME,
                        0
                ))
                .andExpect(header().string(
                        "Set-Cookie",
                        org.hamcrest.Matchers.allOf(
                                org.hamcrest.Matchers.containsString("Path=/"),
                                org.hamcrest.Matchers.containsString("HttpOnly"),
                                org.hamcrest.Matchers.containsString("SameSite=Lax")
                        )
                ));

        mockMvc.perform(get("/home"))
                .andExpect(status().isUnauthorized());
    }

    private Cookie accessTokenCookie(User user) {
        return new Cookie(
                JwtCookieProvider.ACCESS_TOKEN_COOKIE_NAME,
                jwtTokenProvider.createAccessToken(user)
        );
    }
}
