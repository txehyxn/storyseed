package com.taehyun.storyseed.story.controller;

import com.taehyun.storyseed.config.jwt.JwtCookieProvider;
import com.taehyun.storyseed.config.jwt.JwtTokenProvider;
import com.taehyun.storyseed.story.domain.Genre;
import com.taehyun.storyseed.story.domain.Story;
import com.taehyun.storyseed.story.dto.CreateStoryRequest;
import com.taehyun.storyseed.story.dto.StoryDetailView;
import com.taehyun.storyseed.story.service.StoryService;
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

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
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
    private Cookie accessTokenCookie;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        User user = userRepository.save(
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
    void newStoryPageShowsGenreSelectionWithoutTitleOrThemeInputs() throws Exception {
        mockMvc.perform(get("/stories/new").cookie(accessTokenCookie))
                .andExpect(status().isOk())
                .andExpect(view().name("story/new"))
                .andExpect(model().attributeExists("request", "genres"))
                .andExpect(content().string(containsString("data-value=\"FANTASY\"")))
                .andExpect(content().string(containsString("판타지")))
                .andExpect(content().string(containsString("name=\"_csrf\"")))
                .andExpect(content().string(not(containsString("name=\"title\""))))
                .andExpect(content().string(not(containsString("name=\"theme\""))));
    }

    @Test
    void createStoryRequiresAuthentication() throws Exception {
        mockMvc.perform(post("/stories")
                        .with(csrf())
                        .param("genres", "FANTASY"))
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
                        .param("genres", "FANTASY", "MYSTERY", "HORROR"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/stories/10"));

        verify(storyService).createStory(any(User.class), any(CreateStoryRequest.class));
    }

    @Test
    void createStoryRejectsRequestWithoutCsrfToken() throws Exception {
        mockMvc.perform(post("/stories")
                        .cookie(accessTokenCookie)
                        .param("genres", "FANTASY"))
                .andExpect(status().isForbidden());

        verify(storyService, never()).createStory(any(), any());
    }

    @Test
    void createStoryRejectsMissingGenres() throws Exception {
        mockMvc.perform(post("/stories")
                        .cookie(accessTokenCookie)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("story/new"))
                .andExpect(model().attributeHasFieldErrors("request", "genres"));

        verify(storyService, never()).createStory(any(), any());
    }

    @Test
    void createStoryRejectsMoreThanThreeGenres() throws Exception {
        mockMvc.perform(post("/stories")
                        .cookie(accessTokenCookie)
                        .with(csrf())
                        .param(
                                "genres",
                                "FANTASY",
                                "ADVENTURE",
                                "MYSTERY",
                                "HORROR"
                        ))
                .andExpect(status().isOk())
                .andExpect(view().name("story/new"))
                .andExpect(model().attributeHasFieldErrors("request", "genres"));

        verify(storyService, never()).createStory(any(), any());
    }

    @Test
    void createStoryRejectsDuplicateGenres() throws Exception {
        mockMvc.perform(post("/stories")
                        .cookie(accessTokenCookie)
                        .with(csrf())
                        .param("genres", "FANTASY", "FANTASY"))
                .andExpect(status().isOk())
                .andExpect(view().name("story/new"))
                .andExpect(model().hasErrors());

        verify(storyService, never()).createStory(any(), any());
    }

    @Test
    void storyDetailRendersUntitledStoryWithOpeningAndChoices() throws Exception {
        String longOpening = "상세 화면에 표시할 긴 이야기 본문입니다.\n\n".repeat(20);
        StoryDetailView detail = new StoryDetailView(
                10L,
                null,
                "판타지 · 추리 이야기",
                com.taehyun.storyseed.story.domain.StoryStatus.IN_PROGRESS,
                Genre.FANTASY,
                List.of(Genre.MYSTERY),
                List.of(new StoryDetailView.ChapterView(
                        1,
                        longOpening,
                        List.of(new StoryDetailView.ChoiceView(
                                1L,
                                1,
                                "단서를 조사한다."
                        ))
                ))
        );
        when(storyService.getStoryDetail(any(User.class), eq(10L))).thenReturn(detail);

        mockMvc.perform(get("/stories/10").cookie(accessTokenCookie))
                .andExpect(status().isOk())
                .andExpect(view().name("story/detail"))
                .andExpect(content().string(containsString("판타지 · 추리 이야기")))
                .andExpect(content().string(containsString("상세 화면에 표시할 긴 이야기 본문입니다.")))
                .andExpect(content().string(containsString("단서를 조사한다.")))
                .andExpect(content().string(containsString("class=\"choice-section\"")))
                .andExpect(content().string(containsString("어떻게 행동할까요?")));
    }

    @Test
    void storyDetailRejectsStoryOwnedByAnotherUser() throws Exception {
        when(storyService.getStoryDetail(any(User.class), eq(10L)))
                .thenThrow(new IllegalArgumentException("story not found"));

        mockMvc.perform(get("/stories/10").cookie(accessTokenCookie))
                .andExpect(status().isBadRequest());
    }
}
