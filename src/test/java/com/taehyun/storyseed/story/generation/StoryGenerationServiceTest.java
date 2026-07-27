package com.taehyun.storyseed.story.generation;

import com.taehyun.storyseed.story.domain.Genre;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
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
}
