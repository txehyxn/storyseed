package com.taehyun.storyseed.story.generation;

import com.taehyun.storyseed.story.domain.Genre;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StoryGenerationRequestTest {

    @Test
    void storySeedFactoryNormalizesTextAndDefensivelyCopiesGenres() {
        List<Genre> genres = new ArrayList<>(List.of(Genre.MYSTERY));

        StoryGenerationRequest request =
                StoryGenerationRequest.storySeed(genres, "  비가   멈추지 않는 도시  ");
        genres.clear();

        assertEquals(StoryGenerationMode.STORY_SEED, request.mode());
        assertEquals(List.of(Genre.MYSTERY), request.genres());
        assertEquals("비가 멈추지 않는 도시", request.seedText());
        assertThrows(
                UnsupportedOperationException.class,
                () -> request.genres().add(Genre.FANTASY)
        );
    }

    @Test
    void existingFactoryStillCreatesGenreMode() {
        StoryGenerationRequest request =
                StoryGenerationRequest.genre(List.of(Genre.FANTASY));

        assertEquals(StoryGenerationMode.GENRE, request.mode());
        assertEquals(List.of(Genre.FANTASY), request.genres());
    }
}
