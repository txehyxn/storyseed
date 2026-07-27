package com.taehyun.storyseed.story.service;

import com.taehyun.storyseed.story.domain.Chapter;
import com.taehyun.storyseed.story.domain.Choice;
import com.taehyun.storyseed.story.domain.Genre;
import com.taehyun.storyseed.story.domain.Story;
import com.taehyun.storyseed.story.dto.CreateStoryRequest;
import com.taehyun.storyseed.story.dto.CreateCustomWorldRequest;
import com.taehyun.storyseed.story.dto.CreateAiRecommendationRequest;
import com.taehyun.storyseed.story.generation.GeneratedStoryResult;
import com.taehyun.storyseed.story.generation.StoryGenerationMode;
import com.taehyun.storyseed.story.generation.StoryGenerationRequest;
import com.taehyun.storyseed.story.generation.WorldEra;
import com.taehyun.storyseed.story.generation.WorldTheme;
import com.taehyun.storyseed.story.generation.ProtagonistType;
import com.taehyun.storyseed.story.generation.RecommendationMood;
import com.taehyun.storyseed.story.generation.StoryPacing;
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
        when(storyGenerationService.generate(any(StoryGenerationRequest.class)))
                .thenReturn(new GeneratedStoryResult(
                        null,
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
        ArgumentCaptor<StoryGenerationRequest> generationRequestCaptor =
                ArgumentCaptor.forClass(StoryGenerationRequest.class);
        verify(storyGenerationService).generate(generationRequestCaptor.capture());
        assertEquals(StoryGenerationMode.GENRE, generationRequestCaptor.getValue().mode());
        assertEquals(request.genres(), generationRequestCaptor.getValue().genres());

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
    void createClassicRemakeStoryUsesExistingChapterAndChoicePipeline() {
        User user = createUser();
        CreateStoryRequest request = new CreateStoryRequest(List.of(Genre.FANTASY));
        when(storyRepository.save(any(Story.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(storyGenerationService.generate(any(StoryGenerationRequest.class)))
                .thenReturn(new GeneratedStoryResult(
                "흥부와 놀부: 놀부의 마지막 선택",
                "흥부와 놀부의 악역은 오늘도 자신이 옳다고 믿었다.",
                List.of("흥부를 찾아간다.", "제비의 행방을 조사한다.")
        ));
        when(chapterRepository.save(any(Chapter.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Story result = storyService.createClassicRemakeStory(
                user,
                request,
                "흥부와 놀부",
                "villain"
        );

        assertNull(result.getTitle());
        assertEquals(List.of(Genre.FANTASY), result.getGenres());
        verify(storyRepository).updateTitle(
                result.getId(),
                "흥부와 놀부: 놀부의 마지막 선택"
        );
        ArgumentCaptor<StoryGenerationRequest> generationRequestCaptor =
                ArgumentCaptor.forClass(StoryGenerationRequest.class);
        verify(storyGenerationService).generate(generationRequestCaptor.capture());
        StoryGenerationRequest generationRequest = generationRequestCaptor.getValue();
        assertEquals(StoryGenerationMode.CLASSIC_REMAKE, generationRequest.mode());
        assertEquals(List.of(Genre.FANTASY), generationRequest.genres());
        assertEquals("흥부와 놀부", generationRequest.classicTitle());
        assertEquals("villain", generationRequest.remakeType());
        verify(chapterRepository).save(any(Chapter.class));
        verify(choiceRepository, org.mockito.Mockito.times(2)).save(any(Choice.class));
    }

    @Test
    void createCustomWorldStoryUsesCustomWorldGeneratorAndExistingPipeline() {
        User user = createUser();
        CreateCustomWorldRequest request = new CreateCustomWorldRequest(
                "아르카디아",
                WorldTheme.FANTASY,
                WorldEra.MEDIEVAL,
                "카인"
        );
        when(storyRepository.save(any(Story.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(storyGenerationService.generate(any(StoryGenerationRequest.class)))
                .thenReturn(new GeneratedStoryResult(
                        "아르카디아: 붉은 달의 시작",
                        "아르카디아에서는 붉은 달이 떠올랐다.",
                        List.of("붉은 숲으로 향한다.", "왕도로 들어간다.")
                ));
        when(chapterRepository.save(any(Chapter.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Story result = storyService.createCustomWorldStory(user, request);

        assertEquals(List.of(Genre.FANTASY), result.getGenres());
        ArgumentCaptor<StoryGenerationRequest> generationRequestCaptor =
                ArgumentCaptor.forClass(StoryGenerationRequest.class);
        verify(storyGenerationService).generate(generationRequestCaptor.capture());
        StoryGenerationRequest generationRequest = generationRequestCaptor.getValue();
        assertEquals(StoryGenerationMode.CUSTOM_WORLD, generationRequest.mode());
        assertEquals("아르카디아", generationRequest.worldName());
        assertEquals(WorldTheme.FANTASY, generationRequest.worldTheme());
        assertEquals(WorldEra.MEDIEVAL, generationRequest.worldEra());
        assertEquals("카인", generationRequest.protagonistName());
        verify(storyRepository).updateTitle(
                result.getId(),
                "아르카디아: 붉은 달의 시작"
        );
        verify(chapterRepository).save(any(Chapter.class));
        verify(choiceRepository, org.mockito.Mockito.times(2)).save(any(Choice.class));
    }

    @Test
    void createAiRecommendationStoryUsesPreferencesAndExistingPipeline() {
        User user = createUser();
        CreateAiRecommendationRequest request = new CreateAiRecommendationRequest(
                RecommendationMood.MYSTERIOUS,
                StoryPacing.BALANCED,
                ProtagonistType.SECRETIVE
        );
        when(storyRepository.save(any(Story.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(storyGenerationService.generate(any(StoryGenerationRequest.class)))
                .thenReturn(new GeneratedStoryResult(
                        "달이 사라진 밤의 아이",
                        "밤마다 열리는 문 앞에 비밀을 가진 인물이 섰다.",
                        List.of("문을 연다.", "안내자를 조사한다.")
                ));
        when(chapterRepository.save(any(Chapter.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Story result = storyService.createAiRecommendationStory(user, request);

        assertEquals(List.of(Genre.FANTASY), result.getGenres());
        ArgumentCaptor<StoryGenerationRequest> generationRequestCaptor =
                ArgumentCaptor.forClass(StoryGenerationRequest.class);
        verify(storyGenerationService).generate(generationRequestCaptor.capture());
        StoryGenerationRequest generationRequest = generationRequestCaptor.getValue();
        assertEquals(StoryGenerationMode.AI_RECOMMENDATION, generationRequest.mode());
        assertEquals(RecommendationMood.MYSTERIOUS, generationRequest.recommendationMood());
        assertEquals(StoryPacing.BALANCED, generationRequest.storyPacing());
        assertEquals(ProtagonistType.SECRETIVE, generationRequest.protagonistType());
        verify(storyRepository).updateTitle(result.getId(), "달이 사라진 밤의 아이");
        verify(chapterRepository).save(any(Chapter.class));
        verify(choiceRepository, org.mockito.Mockito.times(2)).save(any(Choice.class));
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
