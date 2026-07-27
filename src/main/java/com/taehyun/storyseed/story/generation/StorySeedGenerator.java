package com.taehyun.storyseed.story.generation;

import com.taehyun.storyseed.story.domain.Genre;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StorySeedGenerator implements StoryGenerator {

    @Override
    public StoryGenerationMode mode() {
        return StoryGenerationMode.STORY_SEED;
    }

    @Override
    public GeneratedStoryResult generate(StoryGenerationRequest request) {
        String seedText = request.seedText();
        Genre genre = request.genres().get(0);

        return new GeneratedStoryResult(
                generateTitle(seedText, genre),
                generateBody(seedText, genre),
                generateChoices(seedText, genre)
        );
    }

    private String generateTitle(String seedText, Genre genre) {
        String subtitle = switch (seedText) {
            case "비가 멈추지 않는 도시" -> "마지막 우산";
            case "기억을 파는 상점" -> "잃어버린 이름";
            case "달에서 온 고양이" -> "지구에서의 첫날";
            case "시간을 되돌리는 시계" -> "열세 번째 자정";
            default -> defaultSubtitle(genre);
        };
        return seedText + ": " + subtitle;
    }

    private String defaultSubtitle(Genre genre) {
        return switch (genre) {
            case FANTASY -> "깨어난 비밀";
            case ADVENTURE -> "지도 밖의 길";
            case MYSTERY -> "사라진 단서";
            case HORROR -> "문밖의 기척";
            case SCIENCE_FICTION -> "미지의 신호";
            case ROMANCE -> "다시 만난 순간";
            case SLICE_OF_LIFE -> "작은 변화의 시작";
            case COMEDY -> "예상 밖의 하루";
        };
    }

    private String generateBody(String seedText, Genre genre) {
        return switch (genre) {
            case FANTASY -> seedText + "에는 아무도 설명하지 못한 마법이 잠들어 있었다.\n\n"
                    + "주인공은 오래된 물건에서 자신의 이름을 부르는 목소리를 듣고, "
                    + "굳게 봉인된 문 앞에 섰다.\n\n"
                    + "문이 열리자 현실과 닮았지만 전혀 다른 세계가 모습을 드러냈다.";
            case ADVENTURE -> seedText + "은 아직 어떤 지도에도 표시되지 않은 곳이었다.\n\n"
                    + "주인공은 낡은 나침반이 가리키는 길을 따라 첫발을 내디뎠고, "
                    + "길이 끊긴 절벽에서 숨겨진 통로를 발견했다.\n\n"
                    + "통로 너머에서는 누군가 먼저 남긴 구조 신호가 깜박이고 있었다.";
            case MYSTERY -> seedText + "은 모두에게 익숙했지만, 누구도 그 비밀을 말하지 않았다.\n\n"
                    + "주인공은 매일 같은 시간에 반복되는 수상한 흔적과 "
                    + "발신인이 없는 기록 한 장을 발견했다.\n\n"
                    + "기록의 마지막 줄에는 오늘 날짜와 주인공의 이름이 적혀 있었다.";
            case HORROR -> seedText + "에 해가 지면 아무도 창문을 열지 않았다.\n\n"
                    + "주인공은 빈 방에서 들려오는 발소리를 따라가다가 "
                    + "벽 안쪽에서 자신의 목소리를 들었다.\n\n"
                    + "곧 문손잡이가 방 안이 아니라 벽 너머에서 천천히 돌아가기 시작했다.";
            case SCIENCE_FICTION -> seedText + "은 관측 장비에 존재하지 않는 신호를 보내고 있었다.\n\n"
                    + "주인공은 신호가 미래의 자신이 남긴 좌표라는 사실을 알아냈고, "
                    + "폐쇄된 연구 구역으로 향했다.\n\n"
                    + "그곳의 화면에는 아직 일어나지 않은 도시의 정전이 재생되고 있었다.";
            case ROMANCE -> seedText + "은 두 사람이 오래전 함께 나누었던 약속이었다.\n\n"
                    + "주인공은 우연히 다시 만난 옛 인연에게서 "
                    + "끝내 전하지 못했던 편지를 건네받았다.\n\n"
                    + "편지에는 둘이 함께 해결해야 할 작은 사건의 단서가 남아 있었다.";
            case SLICE_OF_LIFE -> seedText + "은 평범한 하루에 찾아온 작은 변화였다.\n\n"
                    + "주인공은 늘 지나치던 이웃에게 처음으로 말을 걸었고, "
                    + "학교와 직장 사이에서 잊고 있던 물건 하나를 돌려받았다.\n\n"
                    + "그 물건은 오늘 하루를 조금 다르게 보내게 만들었다.";
            case COMEDY -> seedText + "은 사소한 오해 하나로 시작됐다.\n\n"
                    + "주인공은 엉뚱한 물건을 잘못 가져간 탓에 "
                    + "처음 보는 동행과 온 동네를 뛰어다니게 됐다.\n\n"
                    + "문제는 그 물건의 진짜 주인이 바로 뒤에서 따라오고 있다는 점이었다.";
        };
    }

    private List<String> generateChoices(String seedText, Genre genre) {
        if ("비가 멈추지 않는 도시".equals(seedText) && genre == Genre.MYSTERY) {
            return List.of(
                    "편지에 적힌 주소로 향한다.",
                    "빗방울이 거꾸로 흐르는 시간을 기다린다."
            );
        }
        if ("기억을 파는 상점".equals(seedText) && genre == Genre.FANTASY) {
            return List.of(
                    "가장 오래된 기억을 산다.",
                    "주인에게 잃어버린 이름을 묻는다."
            );
        }
        if ("달에서 온 고양이".equals(seedText) && genre == Genre.COMEDY) {
            return List.of(
                    "고양이를 평범한 반려동물처럼 숨긴다.",
                    "고양이가 찾는 우주선을 함께 찾는다."
            );
        }

        return switch (genre) {
            case FANTASY -> List.of("봉인된 문을 연다.", "신비한 물건의 목소리에 답한다.");
            case ADVENTURE -> List.of("나침반이 가리키는 길로 간다.", "구조 신호의 주인을 찾는다.");
            case MYSTERY -> List.of("숨겨진 기록을 조사한다.", "수상한 흔적을 따라간다.");
            case HORROR -> List.of("벽 너머의 목소리에 답한다.", "불을 켜고 방을 빠져나간다.");
            case SCIENCE_FICTION -> List.of("미지의 신호를 해독한다.", "폐쇄된 연구 구역에 들어간다.");
            case ROMANCE -> List.of("오래된 편지를 함께 읽는다.", "전하지 못한 마음을 먼저 묻는다.");
            case SLICE_OF_LIFE -> List.of("이웃에게 물건의 사연을 묻는다.", "평소와 다른 길로 돌아간다.");
            case COMEDY -> List.of("물건을 원래 주인에게 돌려준다.", "엉뚱한 동행의 계획을 따른다.");
        };
    }
}
