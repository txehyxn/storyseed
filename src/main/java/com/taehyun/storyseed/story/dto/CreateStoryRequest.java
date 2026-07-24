package com.taehyun.storyseed.story.dto;

import com.taehyun.storyseed.story.domain.Genre;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.List;

public record CreateStoryRequest(
        @NotEmpty(message = "장르를 하나 이상 선택해주세요.")
        @Size(max = 3, message = "장르는 최대 3개까지 선택할 수 있습니다.")
        List<Genre> genres
) {

    @AssertTrue(message = "같은 장르를 중복해서 선택할 수 없습니다.")
    public boolean isGenreSelectionDistinct() {
        return genres == null || new HashSet<>(genres).size() == genres.size();
    }
}
