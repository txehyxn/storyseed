package com.taehyun.storyseed.story.generation;

import com.taehyun.storyseed.story.domain.Genre;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StoryGenerationServiceTest {

    @Test
    void genreModeSelectsOnlyGenreStoryGenerator() {
        StoryGenerator genreGenerator = mock(StoryGenerator.class);
        StoryGenerator classicGenerator = mock(StoryGenerator.class);
        when(genreGenerator.mode()).thenReturn(StoryGenerationMode.GENRE);
        when(classicGenerator.mode()).thenReturn(StoryGenerationMode.CLASSIC_REMAKE);
        StoryGenerationService service = new StoryGenerationService(
                List.of(genreGenerator, classicGenerator)
        );
        StoryGenerationRequest request =
                StoryGenerationRequest.genre(List.of(Genre.FANTASY));
        GeneratedStoryResult expected = new GeneratedStoryResult(
                null,
                "장르 이야기",
                List.of("선택 1", "선택 2")
        );
        when(genreGenerator.generate(request)).thenReturn(expected);

        GeneratedStoryResult result = service.generate(request);

        assertSame(expected, result);
        verify(genreGenerator).generate(request);
        verify(classicGenerator, never()).generate(request);
    }

    @Test
    void classicModeSelectsOnlyClassicRemakeStoryGenerator() {
        StoryGenerator genreGenerator = mock(StoryGenerator.class);
        StoryGenerator classicGenerator = mock(StoryGenerator.class);
        when(genreGenerator.mode()).thenReturn(StoryGenerationMode.GENRE);
        when(classicGenerator.mode()).thenReturn(StoryGenerationMode.CLASSIC_REMAKE);
        StoryGenerationService service = new StoryGenerationService(
                List.of(genreGenerator, classicGenerator)
        );
        StoryGenerationRequest request = StoryGenerationRequest.classicRemake(
                List.of(Genre.FANTASY),
                "흥부와 놀부",
                "villain"
        );
        GeneratedStoryResult expected = new GeneratedStoryResult(
                "흥부와 놀부: 놀부의 마지막 선택",
                "놀부는 자신이 옳다고 믿었다.",
                List.of("흥부를 찾는다.", "돌아간다.")
        );
        when(classicGenerator.generate(request)).thenReturn(expected);

        GeneratedStoryResult result = service.generate(request);

        assertSame(expected, result);
        verify(classicGenerator).generate(request);
        verify(genreGenerator, never()).generate(request);
    }

    @Test
    void aiRecommendationModeSelectsOnlyAiRecommendationGenerator() {
        StoryGenerator genreGenerator = mock(StoryGenerator.class);
        StoryGenerator recommendationGenerator = mock(StoryGenerator.class);
        when(genreGenerator.mode()).thenReturn(StoryGenerationMode.GENRE);
        when(recommendationGenerator.mode())
                .thenReturn(StoryGenerationMode.AI_RECOMMENDATION);
        StoryGenerationService service = new StoryGenerationService(
                List.of(genreGenerator, recommendationGenerator)
        );
        StoryGenerationRequest request = StoryGenerationRequest.aiRecommendation(
                List.of(Genre.FANTASY),
                RecommendationMood.MYSTERIOUS,
                StoryPacing.BALANCED,
                ProtagonistType.SECRETIVE
        );
        GeneratedStoryResult expected = new GeneratedStoryResult(
                "달이 사라진 밤의 아이",
                "밤마다 문이 열렸다.",
                List.of("문을 연다.", "안내자를 조사한다.")
        );
        when(recommendationGenerator.generate(request)).thenReturn(expected);

        GeneratedStoryResult result = service.generate(request);

        assertSame(expected, result);
        verify(recommendationGenerator).generate(request);
        verify(genreGenerator, never()).generate(request);
    }

    @Test
    void duplicateModeRegistrationIsRejected() {
        StoryGenerator first = mock(StoryGenerator.class);
        StoryGenerator second = mock(StoryGenerator.class);
        when(first.mode()).thenReturn(StoryGenerationMode.AI_RECOMMENDATION);
        when(second.mode()).thenReturn(StoryGenerationMode.AI_RECOMMENDATION);

        assertThrows(
                IllegalStateException.class,
                () -> new StoryGenerationService(List.of(first, second))
        );
    }
}
