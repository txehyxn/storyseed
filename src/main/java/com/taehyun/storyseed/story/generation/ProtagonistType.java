package com.taehyun.storyseed.story.generation;

public enum ProtagonistType {
    ORDINARY_BRAVE("평범하지만 용감한 인물"),
    SECRETIVE("비밀을 가진 인물"),
    GENIUS("천재적인 인물"),
    CLUMSY_GROWTH("실수 많지만 성장하는 인물"),
    JUST_HERO("정의로운 영웅");

    private final String displayName;

    ProtagonistType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
