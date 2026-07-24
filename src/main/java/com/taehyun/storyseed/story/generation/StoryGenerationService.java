package com.taehyun.storyseed.story.generation;

import com.taehyun.storyseed.story.domain.Story;

public interface StoryGenerationService {

    GeneratedChapterResult generateOpening(Story story);
}
