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
}
