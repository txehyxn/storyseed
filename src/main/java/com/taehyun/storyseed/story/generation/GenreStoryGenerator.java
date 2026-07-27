package com.taehyun.storyseed.story.generation;

import org.springframework.stereotype.Component;

@Component
public class GenreStoryGenerator implements StoryGenerator {

    private final MockStoryGenerationService mockStoryGenerationService;

    public GenreStoryGenerator(MockStoryGenerationService mockStoryGenerationService) {
        this.mockStoryGenerationService = mockStoryGenerationService;
    }

    @Override
    public StoryGenerationMode mode() {
        return StoryGenerationMode.GENRE;
    }

    @Override
    public GeneratedStoryResult generate(StoryGenerationRequest request) {
        GeneratedChapterResult opening =
                mockStoryGenerationService.generateOpening(request.genres());

        return new GeneratedStoryResult(
                null,
                opening.content(),
                opening.choices()
        );
    }
}
