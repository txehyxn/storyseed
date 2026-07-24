package com.taehyun.storyseed.story.service;

import com.taehyun.storyseed.story.domain.Story;
import com.taehyun.storyseed.story.dto.CreateStoryRequest;
import com.taehyun.storyseed.story.repository.StoryRepository;
import com.taehyun.storyseed.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StoryServiceTest {

    @Mock
    private StoryRepository storyRepository;

    private StoryService storyService;

    @BeforeEach
    void setUp() {
        storyService = new StoryService(storyRepository);
    }

    @Test
    void createStoryCreatesAndSavesStoryForUser() {
        User user = createUser();
        CreateStoryRequest request = new CreateStoryRequest(
                "  모험 이야기  ",
                "  잃어버린 왕국 탐험  "
        );
        when(storyRepository.save(any(Story.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Story result = storyService.createStory(user, request);

        ArgumentCaptor<Story> storyCaptor = ArgumentCaptor.forClass(Story.class);
        verify(storyRepository).save(storyCaptor.capture());
        Story savedStory = storyCaptor.getValue();

        assertSame(savedStory, result);
        assertSame(user, savedStory.getUser());
        assertEquals("모험 이야기", savedStory.getTitle());
        assertEquals("잃어버린 왕국 탐험", savedStory.getTheme());
    }

    @Test
    void createStoryRejectsUnauthenticatedUserWithoutSaving() {
        CreateStoryRequest request = new CreateStoryRequest("모험 이야기", "왕국 탐험");

        assertThrows(
                IllegalArgumentException.class,
                () -> storyService.createStory(null, request)
        );

        verify(storyRepository, never()).save(any(Story.class));
    }

    private static User createUser() {
        return User.createLocal("user@example.com", "encoded-password", "storyteller");
    }
}
