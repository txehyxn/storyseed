package com.taehyun.storyseed.story.controller;

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

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@ActiveProfiles("test")
class StoryStartControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private MockMvc mockMvc;
    private Cookie accessTokenCookie;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        User user = userRepository.save(
                User.createLocal("start@example.com", "encoded-password", "starter")
        );
        accessTokenCookie = new Cookie(
                JwtCookieProvider.ACCESS_TOKEN_COOKIE_NAME,
                jwtTokenProvider.createAccessToken(user)
        );
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    void startPageRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/story/start"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void startPageShowsAllStoryModesAndExistingGenreLink() throws Exception {
        mockMvc.perform(get("/story/start").cookie(accessTokenCookie))
                .andExpect(status().isOk())
                .andExpect(view().name("story/start"))
                .andExpect(content().string(containsString("어떤 방식으로 이야기를 시작할까요?")))
                .andExpect(content().string(containsString("새로운 이야기 만들기")))
                .andExpect(content().string(containsString("명작 다시 쓰기")))
                .andExpect(content().string(containsString("나만의 세계 만들기")))
                .andExpect(content().string(containsString("AI 추천 이야기")))
                .andExpect(content().string(containsString("이야기 씨앗으로 만들기")))
                .andExpect(content().string(containsString("이어서 쓰기")))
                .andExpect(content().string(containsString("href=\"/stories/new\"")))
                .andExpect(content().string(containsString("사용 가능")))
                .andExpect(content().string(containsString("준비 중")));
    }

    @Test
    void existingGenreSelectionPageStillWorks() throws Exception {
        mockMvc.perform(get("/stories/new").cookie(accessTokenCookie))
                .andExpect(status().isOk())
                .andExpect(view().name("story/new"));
    }

    @Test
    void comingSoonPageShowsRequestedMode() throws Exception {
        mockMvc.perform(get("/story/coming-soon")
                        .queryParam("mode", "classic")
                        .cookie(accessTokenCookie))
                .andExpect(status().isOk())
                .andExpect(view().name("story/coming-soon"))
                .andExpect(content().string(containsString("명작 다시 쓰기")))
                .andExpect(content().string(containsString("준비 중")));
    }

    @Test
    void comingSoonPageHandlesUnknownMode() throws Exception {
        mockMvc.perform(get("/story/coming-soon")
                        .queryParam("mode", "unknown")
                        .cookie(accessTokenCookie))
                .andExpect(status().isOk())
                .andExpect(view().name("story/coming-soon"))
                .andExpect(content().string(containsString("새로운 이야기 기능")));
    }

    @Test
    void homePageLinksToStoryStart() throws Exception {
        mockMvc.perform(get("/home").cookie(accessTokenCookie))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("href=\"/story/start\"")));
    }
}
