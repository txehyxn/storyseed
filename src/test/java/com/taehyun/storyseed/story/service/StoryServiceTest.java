package com.taehyun.storyseed.story.service;

import com.taehyun.storyseed.story.domain.Chapter;
import com.taehyun.storyseed.story.domain.Choice;
import com.taehyun.storyseed.story.domain.Genre;
import com.taehyun.storyseed.story.domain.Story;
import com.taehyun.storyseed.story.dto.CreateStoryRequest;
import com.taehyun.storyseed.story.generation.GeneratedChapterResult;
import com.taehyun.storyseed.story.generation.StoryGenerationService;
import com.taehyun.storyseed.story.repository.ChapterRepository;
import com.taehyun.storyseed.story.repository.ChoiceRepository;
import com.taehyun.storyseed.story.repository.StoryRepository;
import com.taehyun.storyseed.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
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

    @Mock
    private ChapterRepository chapterRepository;

    @Mock
    private ChoiceRepository choiceRepository;

    @Mock
    private StoryGenerationService storyGenerationService;

    private StoryService storyService;

    @BeforeEach
    void setUp() {
        storyService = new StoryService(
                storyRepository,
                chapterRepository,
                choiceRepository,
                storyGenerationService
        );
    }

    @Test
    void createStorySavesGenresOpeningChapterAndChoices() {
        User user = createUser();
        CreateStoryRequest request = new CreateStoryRequest(
                List.of(Genre.FANTASY, Genre.MYSTERY)
        );
        when(storyRepository.save(any(Story.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        String openingContent = """
                주인공은 평소처럼 작업을 마무리하고 있었다.

                그때 익숙한 장소에서 설명할 수 없는 단서가 나타났다.

                주인공은 단서를 따라갈지 도움을 구할지 결정해야 했다.
                """;
        when(storyGenerationService.generateOpening(any(Story.class)))
                .thenReturn(new GeneratedChapterResult(
                        openingContent,
                        List.of("단서를 조사한다.", "목격자를 찾는다.")
                ));
        when(chapterRepository.save(any(Chapter.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Story result = storyService.createStory(user, request);

        assertSame(user, result.getUser());
        assertNull(result.getTitle());
        assertEquals(
                List.of(Genre.FANTASY, Genre.MYSTERY),
                result.getGenres()
        );

        ArgumentCaptor<Chapter> chapterCaptor = ArgumentCaptor.forClass(Chapter.class);
        verify(chapterRepository).save(chapterCaptor.capture());
        assertEquals(1, chapterCaptor.getValue().getChapterNumber());
        assertEquals(openingContent.trim(), chapterCaptor.getValue().getContent());

        ArgumentCaptor<Choice> choiceCaptor = ArgumentCaptor.forClass(Choice.class);
        verify(choiceRepository, org.mockito.Mockito.times(2)).save(choiceCaptor.capture());
        assertEquals(
                List.of(1, 2),
                choiceCaptor.getAllValues().stream().map(Choice::getChoiceNumber).toList()
        );
    }

    @Test
    void createStoryRejectsUnauthenticatedUserWithoutSaving() {
        CreateStoryRequest request = new CreateStoryRequest(List.of(Genre.FANTASY));

        assertThrows(
                IllegalArgumentException.class,
                () -> storyService.createStory(null, request)
        );

        verify(storyRepository, never()).save(any(Story.class));
    }

    @Test
    void createStoryRejectsDuplicateGenres() {
        CreateStoryRequest request = new CreateStoryRequest(
                List.of(Genre.FANTASY, Genre.FANTASY)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> storyService.createStory(createUser(), request)
        );

        verify(storyRepository, never()).save(any(Story.class));
    }

    private static User createUser() {
        return User.createLocal("user@example.com", "encoded-password", "storyteller");
    }
}
