package com.taehyun.storyseed.story.generation;

import com.taehyun.storyseed.story.domain.Genre;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
