package com.taehyun.storyseed.story.generation;

public enum WorldEra {
    ANCIENT("고대"),
    MEDIEVAL("중세"),
    MODERN("현대"),
    NEAR_FUTURE("근미래"),
    FAR_FUTURE("먼 미래");

    private final String displayName;

    WorldEra(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
