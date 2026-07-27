package com.taehyun.storyseed.story.generation;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CustomWorldStoryGenerator implements StoryGenerator {

    @Override
    public StoryGenerationMode mode() {
        return StoryGenerationMode.CUSTOM_WORLD;
    }

    @Override
    public GeneratedStoryResult generate(StoryGenerationRequest request) {
        String title = request.worldName() + ": " + generateSubtitle(request.worldTheme());
        String content = generateOpening(request) + "\n\n" + generateBody(request);

        return new GeneratedStoryResult(
                title,
                content,
                generateChoices(request.worldTheme())
        );
    }

    private String generateSubtitle(WorldTheme theme) {
        return switch (theme) {
            case FANTASY -> "붉은 달의 시작";
            case SCIENCE_FICTION -> "마지막 항로";
            case MODERN -> "사라진 하루";
            case MARTIAL_ARTS -> "검의 맹세";
            case EASTERN -> "봉인의 새벽";
            case WESTERN -> "잿빛 왕관";
        };
    }

    private String generateOpening(StoryGenerationRequest request) {
        String phenomenon = switch (request.worldTheme()) {
            case FANTASY -> "붉은 달이 뜨는 날마다 세상의 마법 법칙이 바뀌었다.";
            case SCIENCE_FICTION -> "두 개의 태양이 겹치는 날마다 도시의 중앙 시스템이 미래의 신호를 수신했다.";
            case MODERN -> "자정이 지나면 지도에서 사라진 거리가 단 한 시간 동안 모습을 드러냈다.";
            case MARTIAL_ARTS -> "봉인된 검이 울리는 날마다 강호의 세력들이 새로운 주인을 찾아 움직였다.";
            case EASTERN -> "푸른 안개가 내려앉는 날마다 사람과 정령의 경계가 희미해졌다.";
            case WESTERN -> "왕성의 종이 열세 번 울리는 밤마다 오래된 왕관이 다음 계승자를 선택했다.";
        };

        return """
                %s에서는 %s

                오늘, %s 그 변화를 처음 목격했다.
                """.formatted(
                request.worldName(),
                phenomenon,
                withTopic(request.protagonistName())
        ).trim();
    }

    private String generateBody(StoryGenerationRequest request) {
        String themeConflict = switch (request.worldTheme()) {
            case FANTASY -> "금지된 마법의 흔적";
            case SCIENCE_FICTION -> "출처를 알 수 없는 좌표";
            case MODERN -> "도시에서 지워진 사람들의 기록";
            case MARTIAL_ARTS -> "사라진 문파의 검결";
            case EASTERN -> "잠에서 깨어난 수호 정령";
            case WESTERN -> "왕위를 뒤흔들 비밀 문서";
        };

        return """
                이곳은 %s의 %s 분위기를 지닌 세계였다. 익숙한 질서가 흔들리는 가운데, %s은 %s을 발견했다.

                그 단서를 외면하면 평온한 일상으로 돌아갈 수 있었다. 하지만 단서를 따라가면 세계의 규칙이 바뀌는 이유와 자신의 운명이 연결되어 있음을 확인하게 될 터였다.
                """.formatted(
                request.worldEra().getDisplayName(),
                request.worldTheme().getDisplayName(),
                request.protagonistName(),
                themeConflict
        ).trim();
    }

    private List<String> generateChoices(WorldTheme theme) {
        return switch (theme) {
            case FANTASY -> List.of("붉은 숲으로 향한다.", "왕도로 들어간다.");
            case SCIENCE_FICTION -> List.of("미래 신호의 좌표를 추적한다.", "중앙 시스템의 기록실에 접속한다.");
            case MODERN -> List.of("지도에서 사라진 거리로 들어간다.", "실종 기록을 가진 사람을 찾아간다.");
            case MARTIAL_ARTS -> List.of("봉인된 검을 들고 강호로 나간다.", "사라진 문파의 흔적을 조사한다.");
            case EASTERN -> List.of("수호 정령을 따라 안개 속으로 들어간다.", "오래된 사당의 봉인을 확인한다.");
            case WESTERN -> List.of("왕성으로 가 비밀 문서를 공개한다.", "국경의 기사단에 도움을 요청한다.");
        };
    }

    private String withTopic(String word) {
        char last = word.charAt(word.length() - 1);
        boolean hasFinalConsonant =
                last >= 0xAC00 && last <= 0xD7A3 && (last - 0xAC00) % 28 != 0;
        return word + (hasFinalConsonant ? "은" : "는");
    }
}
