package com.taehyun.storyseed.story.generation;

import com.taehyun.storyseed.story.domain.Genre;

import java.util.List;

public record StoryGenerationRequest(
        StoryGenerationMode mode,
        List<Genre> genres,
        String classicTitle,
        String remakeType,
        String worldName,
        WorldTheme worldTheme,
        WorldEra worldEra,
        String protagonistName,
        RecommendationMood recommendationMood,
        StoryPacing storyPacing,
        ProtagonistType protagonistType
) {

    public StoryGenerationRequest {
        genres = List.copyOf(genres);
    }

    public static StoryGenerationRequest genre(List<Genre> genres) {
        return new StoryGenerationRequest(
                StoryGenerationMode.GENRE,
                genres,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
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
                remakeType,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    public static StoryGenerationRequest customWorld(
            List<Genre> genres,
            String worldName,
            WorldTheme worldTheme,
            WorldEra worldEra,
            String protagonistName
    ) {
        return new StoryGenerationRequest(
                StoryGenerationMode.CUSTOM_WORLD,
                genres,
                null,
                null,
                worldName,
                worldTheme,
                worldEra,
                protagonistName,
                null,
                null,
                null
        );
    }

    public static StoryGenerationRequest aiRecommendation(
            List<Genre> genres,
            RecommendationMood recommendationMood,
            StoryPacing storyPacing,
            ProtagonistType protagonistType
    ) {
        return new StoryGenerationRequest(
                StoryGenerationMode.AI_RECOMMENDATION,
                genres,
                null,
                null,
                null,
                null,
                null,
                null,
                recommendationMood,
                storyPacing,
                protagonistType
        );
    }
}
