package com.taehyun.storyseed.story.generation;

import com.taehyun.storyseed.story.domain.Genre;

public enum WorldTheme {
    FANTASY("판타지", Genre.FANTASY),
    SCIENCE_FICTION("SF", Genre.SCIENCE_FICTION),
    MODERN("현대", Genre.SLICE_OF_LIFE),
    MARTIAL_ARTS("무협", Genre.ADVENTURE),
    EASTERN("동양풍", Genre.FANTASY),
    WESTERN("서양풍", Genre.ADVENTURE);

    private final String displayName;
    private final Genre genre;

    WorldTheme(String displayName, Genre genre) {
        this.displayName = displayName;
        this.genre = genre;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Genre getGenre() {
        return genre;
    }
}
