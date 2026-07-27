package com.taehyun.storyseed.story.generation;

public enum RecommendationMood {
    WARM("따뜻한"),
    MYSTERIOUS("신비로운"),
    TENSE("긴장감 있는"),
    CHEERFUL("유쾌한"),
    MOVING("감동적인");

    private final String displayName;

    RecommendationMood(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
