package com.taehyun.storyseed.story.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateStoryRequest(
        @NotBlank(message = "제목은 필수입니다.")
        @Size(max = 100, message = "제목은 100자 이하여야 합니다.")
        String title,

        @NotBlank(message = "주제는 필수입니다.")
        @Size(max = 500, message = "주제는 500자 이하여야 합니다.")
        String theme
) {
}
