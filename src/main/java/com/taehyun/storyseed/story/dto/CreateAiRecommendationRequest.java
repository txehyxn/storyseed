package com.taehyun.storyseed.story.dto;

import com.taehyun.storyseed.story.generation.ProtagonistType;
import com.taehyun.storyseed.story.generation.RecommendationMood;
import com.taehyun.storyseed.story.generation.StoryPacing;
import jakarta.validation.constraints.NotNull;

public record CreateAiRecommendationRequest(
        @NotNull(message = "원하는 분위기를 선택해주세요.")
        RecommendationMood mood,

        @NotNull(message = "이야기 속도를 선택해주세요.")
        StoryPacing pacing,

        @NotNull(message = "주인공 유형을 선택해주세요.")
        ProtagonistType protagonistType
) {
}
