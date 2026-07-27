package com.taehyun.storyseed.story.generation;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AiRecommendationStoryGenerator implements StoryGenerator {

    @Override
    public StoryGenerationMode mode() {
        return StoryGenerationMode.AI_RECOMMENDATION;
    }

    @Override
    public GeneratedStoryResult generate(StoryGenerationRequest request) {
        String title = generateTitle(request.recommendationMood());
        String content = generatePacingOpening(
                request.recommendationMood(),
                request.storyPacing()
        ) + "\n\n" + generateProtagonist(request.protagonistType())
                + "\n\n" + generateIncident(request.recommendationMood());

        return new GeneratedStoryResult(
                title,
                content,
                generateChoices(request.recommendationMood())
        );
    }

    private String generateTitle(RecommendationMood mood) {
        return switch (mood) {
            case WARM -> "별빛 우체국의 마지막 편지";
            case MYSTERIOUS -> "달이 사라진 밤의 아이";
            case TENSE -> "자정까지 남은 일곱 개의 암호";
            case CHEERFUL -> "용을 잘못 깨운 견습 마법사";
            case MOVING -> "봄이 오기 전 지켜야 할 약속";
        };
    }

    private String generatePacingOpening(
            RecommendationMood mood,
            StoryPacing pacing
    ) {
        String background = switch (mood) {
            case WARM -> "작은 마을의 별빛 우체국에는 주인을 잃은 오래된 편지들이 쌓여 있었다.";
            case MYSTERIOUS -> "별이 하나씩 사라지는 밤마다 골목 끝에는 낮에 없던 문이 나타났다.";
            case TENSE -> "도시 전체가 봉쇄된 뒤, 자정까지 풀어야 할 일곱 개의 암호가 전광판에 떠올랐다.";
            case CHEERFUL -> "마을 축제 전날, 배달 실수로 잠들어 있던 작은 용이 광장 한복판에서 깨어났다.";
            case MOVING -> "긴 겨울이 끝나기 전, 오래전에 헤어진 친구가 남긴 약속이 다시 도착했다.";
        };

        return switch (pacing) {
            case SLOW -> background
                    + "\n\n주인공은 주변 사람들의 표정과 오래된 흔적을 천천히 살피며 사건이 시작된 이유를 생각했다.";
            case BALANCED -> background
                    + "\n\n주인공이 상황을 이해하려는 순간, 첫 번째 단서가 예상보다 가까운 곳에서 모습을 드러냈다.";
            case FAST -> "경고음이 울리자 사건은 기다려 주지 않았다.\n\n"
                    + background
                    + " 주인공은 설명을 들을 틈도 없이 첫 번째 선택 앞에 섰다.";
        };
    }

    private String generateProtagonist(ProtagonistType protagonistType) {
        return switch (protagonistType) {
            case ORDINARY_BRAVE ->
                    "주인공에게 특별한 능력은 없었지만, 위험에 처한 사람을 보고도 물러서지 않는 용기가 있었다.";
            case SECRETIVE ->
                    "주인공은 누구에게도 밝힐 수 없는 과거와, 사건의 단서를 알아볼 수 있는 비밀스러운 능력을 숨기고 있었다.";
            case GENIUS ->
                    "주인공은 흩어진 단서들의 규칙을 누구보다 빠르게 분석해 사건 뒤에 다른 의도가 있음을 알아냈다.";
            case CLUMSY_GROWTH ->
                    "주인공은 첫 판단부터 실수했지만 도망치지 않았고, 잘못된 선택을 직접 바로잡기로 했다.";
            case JUST_HERO ->
                    "주인공은 자신의 안전보다 위험에 빠진 사람들을 먼저 지키는 것이 옳다고 믿었다.";
        };
    }

    private String generateIncident(RecommendationMood mood) {
        return switch (mood) {
            case WARM ->
                    "마지막 편지에는 가족과 이웃이 오랫동안 전하지 못한 진심이 적혀 있었고, 해가 지기 전에 주인을 찾아야 했다.";
            case MYSTERIOUS ->
                    "문 너머에서 정체를 알 수 없는 안내자가 사라진 별을 되찾고 싶다면 이름 하나를 포기하라고 속삭였다.";
            case TENSE ->
                    "첫 암호가 풀리자 믿었던 동료의 이름이 배신자 명단에 나타났고, 남은 시간은 빠르게 줄어들었다.";
            case CHEERFUL ->
                    "말하는 고양이는 용을 재우는 임무가 사실 왕실의 잃어버린 케이크를 찾는 일이라고 태연하게 말했다.";
            case MOVING ->
                    "약속을 지키려면 소중한 것을 포기해야 했지만, 그 선택은 헤어진 사람과 다시 만날 유일한 기회이기도 했다.";
        };
    }

    private List<String> generateChoices(RecommendationMood mood) {
        return switch (mood) {
            case WARM -> List.of(
                    "편지의 주인을 직접 찾아간다.",
                    "마을 사람들에게 먼저 도움을 요청한다."
            );
            case MYSTERIOUS -> List.of(
                    "밤마다 나타나는 문을 연다.",
                    "안내자의 정체를 먼저 조사한다."
            );
            case TENSE -> List.of(
                    "남은 암호를 즉시 해독한다.",
                    "동료에게 배신 가능성을 알린다."
            );
            case CHEERFUL -> List.of(
                    "말하는 고양이와 용을 다시 재운다.",
                    "왕실 주방에서 사라진 케이크를 찾는다."
            );
            case MOVING -> List.of(
                    "약속 장소로 혼자 향한다.",
                    "헤어진 사람의 마지막 기록을 먼저 확인한다."
            );
        };
    }
}
