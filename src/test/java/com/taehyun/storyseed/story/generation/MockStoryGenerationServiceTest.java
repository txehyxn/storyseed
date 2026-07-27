package com.taehyun.storyseed.story.generation;

import com.taehyun.storyseed.story.domain.Genre;
import com.taehyun.storyseed.story.domain.Story;
import com.taehyun.storyseed.user.domain.User;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MockStoryGenerationServiceTest {

    private final MockStoryGenerationService generationService =
            new MockStoryGenerationService();

    @Test
    void everyPrimaryGenreCreatesSubstantialSceneAndTwoConcreteChoices() {
        for (Genre genre : Genre.values()) {
            GeneratedChapterResult result = generationService.generateOpening(
                    createStory(List.of(genre))
            );

            assertNotNull(result.content());
            assertFalse(result.content().isBlank());
            assertTrue(result.content().split("\\R\\R").length >= 4);
            assertEquals(2, result.choices().size());
            assertNotEquals(result.choices().get(0), result.choices().get(1));
            assertTrue(result.choices().stream().noneMatch(String::isBlank));
        }
    }

    @Test
    void differentPrimaryGenresCreateDifferentEventsAndChoices() {
        GeneratedChapterResult fantasy = generationService.generateOpening(
                createStory(List.of(Genre.FANTASY))
        );
        GeneratedChapterResult mystery = generationService.generateOpening(
                createStory(List.of(Genre.MYSTERY))
        );

        assertNotEquals(fantasy.content(), mystery.content());
        assertNotEquals(fantasy.choices(), mystery.choices());
        assertTrue(fantasy.content().contains("지도"));
        assertTrue(mystery.content().contains("신고서"));
        assertTrue(fantasy.content().contains("지하 서고"));
        assertTrue(fantasy.choices().get(0).contains("지하 서고"));
        assertTrue(fantasy.content().contains("북문"));
        assertTrue(fantasy.choices().get(1).contains("북문"));
        assertTrue(mystery.content().contains("제한 구역"));
        assertTrue(mystery.choices().get(0).contains("제한 구역"));
        assertTrue(mystery.content().contains("사물함"));
        assertTrue(mystery.choices().get(1).contains("사물함"));
    }

    @Test
    void secondaryGenresChangeAtmosphereWithoutReplacingPrimaryEvent() {
        GeneratedChapterResult adventureOnly = generationService.generateOpening(
                createStory(List.of(Genre.ADVENTURE))
        );
        GeneratedChapterResult combined = generationService.generateOpening(
                createStory(List.of(
                        Genre.ADVENTURE,
                        Genre.SLICE_OF_LIFE,
                        Genre.FANTASY
                ))
        );

        assertNotEquals(adventureOnly.content(), combined.content());
        assertTrue(combined.content().contains("청동 원통"));
        assertTrue(combined.content().contains("익숙한 이웃"));
        assertTrue(combined.content().contains("설명할 수 없는 빛"));
    }

    @Test
    void supportedGenreCombinationsDoNotReturnSameOpening() {
        List<GeneratedChapterResult> results = List.of(
                generationService.generateOpening(createStory(List.of(Genre.ADVENTURE))),
                generationService.generateOpening(createStory(List.of(
                        Genre.ADVENTURE,
                        Genre.SLICE_OF_LIFE,
                        Genre.FANTASY
                ))),
                generationService.generateOpening(createStory(List.of(
                        Genre.MYSTERY,
                        Genre.HORROR
                ))),
                generationService.generateOpening(createStory(List.of(
                        Genre.SCIENCE_FICTION,
                        Genre.ROMANCE,
                        Genre.COMEDY
                )))
        );

        assertEquals(4, results.stream().map(GeneratedChapterResult::content).distinct().count());
    }

    @Test
    void classicRemakeOpeningUsesClassicSpecificBodyAndChoices() {
        Story story = createStory(List.of(Genre.FANTASY));

        GeneratedStoryResult remake = generationService.generateClassicRemakeOpening(
                story,
                "흥부와 놀부",
                "villain"
        );

        assertEquals("흥부와 놀부: 놀부의 마지막 선택", remake.title());
        assertTrue(remake.content().startsWith(
                "놀부는 언제나 세상이 자신에게 불공평하다고 생각했다."
        ));
        assertTrue(remake.content().contains("가난한 흥부와 부유한 놀부"));
        assertTrue(remake.content().contains("다친 제비"));
        assertTrue(remake.content().contains("놀부는 자신의 행동을 정당화"));
        assertTrue(remake.choices().get(0).contains("흥부"));
        assertTrue(remake.choices().get(1).contains("박씨"));
    }

    @Test
    void classicRemakeSubtitleChangesByClassicAndRemakeType() {
        Story story = createStory(List.of(Genre.FANTASY));

        GeneratedStoryResult modernHeungbu =
                generationService.generateClassicRemakeOpening(
                        story,
                        "흥부와 놀부",
                        "era"
                );
        GeneratedStoryResult changedSimcheongEnding =
                generationService.generateClassicRemakeOpening(
                        story,
                        "심청전",
                        "ending"
                );

        assertEquals("흥부와 놀부: 2026년 서울의 형제", modernHeungbu.title());
        assertTrue(modernHeungbu.content().startsWith(
                "2026년 서울.\n\n흥부는 오래된 원룸 창문을 열었다."
        ));
        assertEquals(
                "심청전: 바다에 뛰어들지 않은 심청",
                changedSimcheongEnding.title()
        );
    }

    @Test
    void everyClassicCreatesItsOwnAtmosphereAndChoices() {
        Story story = createStory(List.of(Genre.FANTASY));
        List<String> classicTitles = List.of(
                "흥부와 놀부",
                "콩쥐팥쥐",
                "홍길동전",
                "심청전",
                "선녀와 나무꾼",
                "별주부전",
                "토끼와 거북이",
                "빨간모자",
                "백설공주",
                "신데렐라"
        );

        List<GeneratedStoryResult> results = classicTitles.stream()
                .map(title -> generationService.generateClassicRemakeOpening(
                        story,
                        title,
                        "hero"
                ))
                .toList();

        assertEquals(10, results.stream().map(GeneratedStoryResult::content).distinct().count());
        assertEquals(10, results.stream().map(GeneratedStoryResult::choices).distinct().count());
        assertTrue(results.stream().allMatch(result -> result.choices().size() == 2));
        assertTrue(results.get(2).content().contains("활빈당"));
        assertTrue(results.get(3).content().contains("공양미 삼백 석"));
        assertTrue(results.get(9).choices().get(0).contains("유리구두"));
    }

    @Test
    void remakeTypesCreateDifferentOpeningsAndDirections() {
        Story story = createStory(List.of(Genre.FANTASY));
        List<String> remakeTypes = List.of("hero", "era", "ending", "villain", "ai");

        List<GeneratedStoryResult> results = remakeTypes.stream()
                .map(type -> generationService.generateClassicRemakeOpening(
                        story,
                        "흥부와 놀부",
                        type
                ))
                .toList();

        assertEquals(5, results.stream().map(GeneratedStoryResult::title).distinct().count());
        assertEquals(5, results.stream().map(GeneratedStoryResult::content).distinct().count());
        assertTrue(results.get(0).content().contains("스스로 운명을"));
        assertTrue(results.get(1).content().startsWith("2026년 서울."));
        assertTrue(results.get(2).content().contains("원작과 다른 결말"));
        assertTrue(results.get(3).content().contains("놀부는 언제나"));
        assertTrue(results.get(4).content().contains("누구의 운명도 정해져 있지 않았다"));
    }

    private static Story createStory(List<Genre> genres) {
        return Story.create(
                User.createLocal(
                        "user@example.com",
                        "encoded-password",
                        "storyteller"
                ),
                genres
        );
    }
}
