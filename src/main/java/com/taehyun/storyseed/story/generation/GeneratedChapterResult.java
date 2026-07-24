package com.taehyun.storyseed.story.generation;

import java.util.List;

public record GeneratedChapterResult(
        String content,
        List<String> choices
) {
}
