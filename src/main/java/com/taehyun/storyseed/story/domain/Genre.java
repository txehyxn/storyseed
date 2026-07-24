package com.taehyun.storyseed.story.domain;

import lombok.Getter;

@Getter
public enum Genre {
    FANTASY("판타지"),
    ADVENTURE("모험"),
    MYSTERY("추리"),
    HORROR("공포"),
    SCIENCE_FICTION("SF"),
    ROMANCE("로맨스"),
    SLICE_OF_LIFE("일상"),
    COMEDY("코미디");

    private final String displayName;

    Genre(String displayName) {
        this.displayName = displayName;
    }
}
