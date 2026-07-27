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
    void classicRemakeOpeningAddsSelectedClassicAndRemakeTypeToExistingOpening() {
        Story story = createStory(List.of(Genre.FANTASY));

        GeneratedChapterResult standard = generationService.generateOpening(story);
        GeneratedStoryResult remake = generationService.generateClassicRemakeOpening(
                story,
                "흥부와 놀부",
                "villain"
        );

        assertEquals("흥부와 놀부: 놀부의 마지막 선택", remake.title());
        assertTrue(remake.content().startsWith(
                "놀부는 오늘도 자신이 옳다고 믿었다."
        ));
        assertTrue(remake.content().contains(standard.content()));
        assertEquals(standard.choices(), remake.choices());
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
                "서울 2026년.\n\n흥부는 작은 원룸에서 하루를 시작했다."
        ));
        assertEquals(
                "심청전: 바다에 뛰어들지 않은 심청",
                changedSimcheongEnding.title()
        );
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
