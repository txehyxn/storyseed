package com.taehyun.storyseed.story.controller;

import com.taehyun.storyseed.story.domain.Story;
import com.taehyun.storyseed.story.dto.CreateStoryRequest;
import com.taehyun.storyseed.story.service.StoryService;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@ActiveProfiles("test")
class StoryControllerTest {

    @Autowired
    private WebApplicationContext context;

    @MockitoBean
    private StoryService storyService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private MockMvc mockMvc;
    private User user;
    private Cookie accessTokenCookie;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        user = userRepository.save(
                User.createLocal("user@example.com", "encoded-password", "storyteller")
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
    void newStoryPageRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/stories/new"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void newStoryPageReturnsFormForAuthenticatedUser() throws Exception {
        mockMvc.perform(get("/stories/new").cookie(accessTokenCookie))
                .andExpect(status().isOk())
                .andExpect(view().name("story/new"))
                .andExpect(model().attributeExists("request"))
                .andExpect(content().string(
                        org.hamcrest.Matchers.containsString("name=\"_csrf\"")
                ));
    }

    @Test
    void createStoryRequiresAuthentication() throws Exception {
        mockMvc.perform(post("/stories")
                        .with(csrf())
                        .param("title", "모험 이야기")
                        .param("theme", "잃어버린 왕국 탐험"))
                .andExpect(status().isUnauthorized());

        verify(storyService, never()).createStory(any(), any());
    }

    @Test
    void createStoryRedirectsToCreatedStory() throws Exception {
        Story story = mock(Story.class);
        when(story.getId()).thenReturn(10L);
        when(storyService.createStory(any(User.class), any(CreateStoryRequest.class)))
                .thenReturn(story);

        mockMvc.perform(post("/stories")
                        .cookie(accessTokenCookie)
                        .with(csrf())
                        .param("title", "모험 이야기")
                        .param("theme", "잃어버린 왕국 탐험"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/stories/10"));

        verify(storyService).createStory(any(User.class), any(CreateStoryRequest.class));
    }

    @Test
    void createStoryRejectsRequestWithoutCsrfToken() throws Exception {
        mockMvc.perform(post("/stories")
                        .cookie(accessTokenCookie)
                        .param("title", "모험 이야기")
                        .param("theme", "잃어버린 왕국 탐험"))
                .andExpect(status().isForbidden());

        verify(storyService, never()).createStory(any(), any());
    }

    @Test
    void createStoryRejectsBlankTitle() throws Exception {
        mockMvc.perform(post("/stories")
                        .cookie(accessTokenCookie)
                        .with(csrf())
                        .param("title", " ")
                        .param("theme", "잃어버린 왕국 탐험"))
                .andExpect(status().isOk())
                .andExpect(view().name("story/new"))
                .andExpect(model().attributeHasFieldErrors("request", "title"));

        verify(storyService, never()).createStory(any(), any());
    }

    @Test
    void createStoryRejectsBlankTheme() throws Exception {
        mockMvc.perform(post("/stories")
                        .cookie(accessTokenCookie)
                        .with(csrf())
                        .param("title", "모험 이야기")
                        .param("theme", " "))
                .andExpect(status().isOk())
                .andExpect(view().name("story/new"))
                .andExpect(model().attributeHasFieldErrors("request", "theme"));

        verify(storyService, never()).createStory(any(), any());
    }

    @Test
    void createStoryRejectsTitleLongerThanOneHundredCharacters() throws Exception {
        mockMvc.perform(post("/stories")
                        .cookie(accessTokenCookie)
                        .with(csrf())
                        .param("title", "가".repeat(101))
                        .param("theme", "잃어버린 왕국 탐험"))
                .andExpect(status().isOk())
                .andExpect(view().name("story/new"))
                .andExpect(model().attributeHasFieldErrors("request", "title"));

        verify(storyService, never()).createStory(any(), any());
    }

    @Test
    void createStoryRejectsThemeLongerThanFiveHundredCharacters() throws Exception {
        mockMvc.perform(post("/stories")
                        .cookie(accessTokenCookie)
                        .with(csrf())
                        .param("title", "모험 이야기")
                        .param("theme", "가".repeat(501)))
                .andExpect(status().isOk())
                .andExpect(view().name("story/new"))
                .andExpect(model().attributeHasFieldErrors("request", "theme"));

        verify(storyService, never()).createStory(any(), any());
    }

    @Test
    void storyDetailRejectsStoryOwnedByAnotherUser() throws Exception {
        when(storyService.getStory(any(User.class), eq(10L)))
                .thenThrow(new IllegalArgumentException("story not found"));

        mockMvc.perform(get("/stories/10").cookie(accessTokenCookie))
                .andExpect(status().isBadRequest());
    }
}
