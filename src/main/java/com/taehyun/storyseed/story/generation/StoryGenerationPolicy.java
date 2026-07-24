package com.taehyun.storyseed.story.generation;

import java.util.List;

public final class StoryGenerationPolicy {

    public static final int TOTAL_CHAPTER_COUNT = 5;

    private static final List<String> RULES = List.of(
            "첫 챕터는 하나의 완성된 장면으로 구성한다.",
            "독자가 현재 상황을 이해할 수 있도록 장소와 주인공의 행동을 보여 준다.",
            "평범함을 깨는 사건과 그에 대한 인물의 반응을 포함한다.",
            "사용자가 왜 선택해야 하는지 드러나는 순간에서 장면을 끝낸다.",
            "장면이 완성되면 바로 선택지를 제시하고 글자 수를 억지로 늘리지 않는다.",
            "같은 설명을 반복하거나 사건과 무관한 묘사를 덧붙이지 않는다.",
            "첫 번째 장르를 이야기의 중심 장르로 사용한다.",
            "보조 장르는 중심 줄거리를 방해하지 않는 범위에서 활용한다.",
            "장르 이름을 설명문처럼 직접 나열하지 않는다.",
            "사용자가 선택해야 하는 이유를 본문에서 충분히 드러낸다.",
            "선택지는 현재 사건에 연결되고 서로 다른 행동과 결과 가능성을 가진다.",
            "선택 직전에는 긴장감이 분명해지도록 질문을 남긴다.",
            "이전 챕터의 사건과 사용자의 선택 결과를 유지한다.",
            "등장인물의 성격, 관계, 목표와 확정된 세계관 설정을 일관되게 유지한다.",
            "서로 의미가 다르고 다음 이야기로 자연스럽게 이어지는 선택지를 만든다.",
            "남은 챕터 수를 고려하고 마지막 챕터에서 주요 갈등과 복선을 정리한다.",
            "이야기가 완결되기 전에는 제목을 생성하지 않는다."
    );

    private StoryGenerationPolicy() {
    }

    public static List<String> rules() {
        return RULES;
    }
}
