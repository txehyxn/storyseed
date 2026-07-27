package com.taehyun.storyseed.story.generation;

import org.springframework.stereotype.Component;

@Component
public class ClassicRemakeStoryGenerator implements StoryGenerator {

    private final MockStoryGenerationService mockStoryGenerationService;

    public ClassicRemakeStoryGenerator(
            MockStoryGenerationService mockStoryGenerationService
    ) {
        this.mockStoryGenerationService = mockStoryGenerationService;
    }

    @Override
    public StoryGenerationMode mode() {
        return StoryGenerationMode.CLASSIC_REMAKE;
    }

    @Override
    public GeneratedStoryResult generate(StoryGenerationRequest request) {
        return mockStoryGenerationService.generateClassicRemakeOpening(
                request.classicTitle(),
                request.remakeType()
        );
    }
}
