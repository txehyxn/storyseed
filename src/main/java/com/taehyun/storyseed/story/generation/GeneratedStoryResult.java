package com.taehyun.storyseed.story.generation;

import java.util.List;

public record GeneratedStoryResult(
        String title,
        String content,
        List<String> choices
) {
}
