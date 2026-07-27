package com.taehyun.storyseed.story.dto;

import com.taehyun.storyseed.story.domain.Genre;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateStorySeedRequest(
        @NotBlank(message = "이야기 씨앗을 입력해 주세요.")
        @Size(min = 2, max = 100, message = "이야기 씨앗은 2자 이상 100자 이하로 입력해 주세요.")
        String seedText,

        @NotNull(message = "원하는 장르를 선택해 주세요.")
        Genre genre
) {
}
