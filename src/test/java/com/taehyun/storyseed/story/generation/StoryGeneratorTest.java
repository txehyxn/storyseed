package com.taehyun.storyseed.story.generation;

import com.taehyun.storyseed.story.domain.Genre;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StoryGeneratorTest {

    private final MockStoryGenerationService mockGenerationService =
            new MockStoryGenerationService();

    @Test
    void genreGeneratorPreservesExistingMockResult() {
        GenreStoryGenerator generator =
                new GenreStoryGenerator(mockGenerationService);
        StoryGenerationRequest request = StoryGenerationRequest.genre(
                List.of(Genre.FANTASY, Genre.MYSTERY)
        );
        GeneratedChapterResult previousResult =
                mockGenerationService.generateOpening(request.genres());

        GeneratedStoryResult result = generator.generate(request);

        assertEquals(previousResult.content(), result.content());
        assertEquals(previousResult.choices(), result.choices());
        assertEquals(null, result.title());
    }

    @Test
    void classicGeneratorPreservesExistingMockResult() {
        ClassicRemakeStoryGenerator generator =
                new ClassicRemakeStoryGenerator(mockGenerationService);
        StoryGenerationRequest request = StoryGenerationRequest.classicRemake(
                List.of(Genre.FANTASY),
                "흥부와 놀부",
                "villain"
        );
        GeneratedStoryResult previousResult =
                mockGenerationService.generateClassicRemakeOpening(
                        request.classicTitle(),
                        request.remakeType()
                );

        GeneratedStoryResult result = generator.generate(request);

        assertEquals(previousResult, result);
    }

    @Test
    void customWorldGeneratorUsesEveryWorldSetting() {
        CustomWorldStoryGenerator generator = new CustomWorldStoryGenerator();
        StoryGenerationRequest request = StoryGenerationRequest.customWorld(
                List.of(Genre.FANTASY),
                "아르카디아",
                WorldTheme.FANTASY,
                WorldEra.MEDIEVAL,
                "카인"
        );

        GeneratedStoryResult result = generator.generate(request);

        assertEquals("아르카디아: 붉은 달의 시작", result.title());
        assertEquals(2, result.choices().size());
        assertEquals("붉은 숲으로 향한다.", result.choices().get(0));
        assertEquals("왕도로 들어간다.", result.choices().get(1));
        assertTrue(result.content().contains("아르카디아"));
        assertTrue(result.content().contains("판타지"));
        assertTrue(result.content().contains("중세"));
        assertTrue(result.content().contains("카인"));
    }

    @Test
    void aiRecommendationGeneratorUsesMoodAndCreatesTwoChoices() {
        AiRecommendationStoryGenerator generator =
                new AiRecommendationStoryGenerator();
        StoryGenerationRequest request = StoryGenerationRequest.aiRecommendation(
                List.of(Genre.FANTASY),
                RecommendationMood.MYSTERIOUS,
                StoryPacing.BALANCED,
                ProtagonistType.SECRETIVE
        );

        GeneratedStoryResult result = generator.generate(request);

        assertEquals(StoryGenerationMode.AI_RECOMMENDATION, generator.mode());
        assertEquals("달이 사라진 밤의 아이", result.title());
        assertEquals(2, result.choices().size());
        assertTrue(result.content().contains("별이 하나씩 사라지는 밤"));
        assertTrue(result.content().contains("비밀스러운 능력"));
        assertTrue(result.choices().get(0).contains("문"));
        assertTrue(result.choices().get(1).contains("안내자"));
        assertTrue(!result.title().isBlank());
        assertTrue(!result.content().isBlank());
    }

    @Test
    void aiRecommendationGeneratorChangesContentByPacing() {
        AiRecommendationStoryGenerator generator =
                new AiRecommendationStoryGenerator();

        GeneratedStoryResult slow = generator.generate(
                StoryGenerationRequest.aiRecommendation(
                        List.of(Genre.SLICE_OF_LIFE),
                        RecommendationMood.WARM,
                        StoryPacing.SLOW,
                        ProtagonistType.ORDINARY_BRAVE
                )
        );
        GeneratedStoryResult fast = generator.generate(
                StoryGenerationRequest.aiRecommendation(
                        List.of(Genre.SLICE_OF_LIFE),
                        RecommendationMood.WARM,
                        StoryPacing.FAST,
                        ProtagonistType.ORDINARY_BRAVE
                )
        );

        assertTrue(slow.content().contains("천천히 살피며"));
        assertTrue(fast.content().startsWith("경고음이 울리자"));
        assertTrue(!slow.content().equals(fast.content()));
    }

    @Test
    void aiRecommendationGeneratorChangesContentByProtagonistType() {
        AiRecommendationStoryGenerator generator =
                new AiRecommendationStoryGenerator();

        GeneratedStoryResult genius = generator.generate(
                StoryGenerationRequest.aiRecommendation(
                        List.of(Genre.MYSTERY),
                        RecommendationMood.TENSE,
                        StoryPacing.BALANCED,
                        ProtagonistType.GENIUS
                )
        );
        GeneratedStoryResult growing = generator.generate(
                StoryGenerationRequest.aiRecommendation(
                        List.of(Genre.MYSTERY),
                        RecommendationMood.TENSE,
                        StoryPacing.BALANCED,
                        ProtagonistType.CLUMSY_GROWTH
                )
        );

        assertTrue(genius.content().contains("누구보다 빠르게 분석"));
        assertTrue(growing.content().contains("첫 판단부터 실수"));
        assertTrue(!genius.content().equals(growing.content()));
    }
}
