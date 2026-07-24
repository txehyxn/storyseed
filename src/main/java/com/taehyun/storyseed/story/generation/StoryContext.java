package com.taehyun.storyseed.story.generation;

import com.taehyun.storyseed.story.domain.Genre;

import java.util.List;

public record StoryContext(
        List<Genre> genres,
        String worldSetting,
        List<String> characters,
        String currentGoal,
        String currentConflict,
        List<String> choiceHistory,
        List<String> establishedFacts,
        List<String> unresolvedForeshadowing,
        int remainingChapterCount
) {
}
