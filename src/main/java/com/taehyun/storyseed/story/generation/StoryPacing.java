package com.taehyun.storyseed.story.generation;

public enum StoryPacing {
    SLOW("느긋한 전개"),
    BALANCED("균형 잡힌 전개"),
    FAST("빠른 전개");

    private final String displayName;

    StoryPacing(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
