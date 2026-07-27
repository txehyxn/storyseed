package com.taehyun.storyseed.story.dto;

import com.taehyun.storyseed.story.generation.WorldEra;
import com.taehyun.storyseed.story.generation.WorldTheme;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateCustomWorldRequest(
        @NotBlank(message = "세계 이름을 입력해주세요.")
        @Size(max = 40, message = "세계 이름은 40자 이하여야 합니다.")
        String worldName,

        @NotNull(message = "세계 분위기를 선택해주세요.")
        WorldTheme worldTheme,

        @NotNull(message = "시대를 선택해주세요.")
        WorldEra worldEra,

        @NotBlank(message = "주인공 이름을 입력해주세요.")
        @Size(max = 30, message = "주인공 이름은 30자 이하여야 합니다.")
        String protagonistName
) {
}
