package com.taehyun.storyseed.story.generation;

import com.taehyun.storyseed.story.domain.Genre;

import java.util.List;

public record StoryGenerationRequest(
        StoryGenerationMode mode,
        List<Genre> genres,
        String classicTitle,
        String remakeType
) {

    public StoryGenerationRequest {
        genres = List.copyOf(genres);
    }

    public static StoryGenerationRequest genre(List<Genre> genres) {
        return new StoryGenerationRequest(
                StoryGenerationMode.GENRE,
                genres,
                null,
                null
        );
    }

    public static StoryGenerationRequest classicRemake(
            List<Genre> genres,
            String classicTitle,
            String remakeType
    ) {
        return new StoryGenerationRequest(
                StoryGenerationMode.CLASSIC_REMAKE,
                genres,
                classicTitle,
                remakeType
        );
    }
}
