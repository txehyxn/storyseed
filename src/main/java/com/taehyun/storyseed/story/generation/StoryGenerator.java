package com.taehyun.storyseed.story.generation;

public interface StoryGenerator {

    StoryGenerationMode mode();

    GeneratedStoryResult generate(StoryGenerationRequest request);
}
