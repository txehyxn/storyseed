package com.taehyun.storyseed.story.domain;

import com.taehyun.storyseed.user.domain.User;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StoryTest {

    @Test
    void createWithOneGenreSetsInitialState() {
        User user = createUser();

        Story story = Story.create(user, List.of(Genre.FANTASY));

        assertSame(user, story.getUser());
        assertNull(story.getTitle());
        assertEquals(List.of(Genre.FANTASY), story.getGenres());
        assertEquals(StoryStatus.IN_PROGRESS, story.getStatus());
    }

    @Test
    void createPreservesGenrePriorityOrder() {
        Story story = Story.create(
                createUser(),
                List.of(Genre.FANTASY, Genre.MYSTERY, Genre.HORROR)
        );

        assertEquals(Genre.FANTASY, story.getPrimaryGenre());
        assertEquals(
                List.of(Genre.MYSTERY, Genre.HORROR),
                story.getSecondaryGenres()
        );
        assertEquals("판타지 · 추리 · 공포 이야기", story.getDisplayTitle());
    }

    @Test
    void createAcceptsTwoAndThreeGenres() {
        Story twoGenres = Story.create(
                createUser(),
                List.of(Genre.ADVENTURE, Genre.COMEDY)
        );
        Story threeGenres = Story.create(
                createUser(),
                List.of(Genre.ROMANCE, Genre.SLICE_OF_LIFE, Genre.COMEDY)
        );

        assertEquals(2, twoGenres.getGenres().size());
        assertEquals(3, threeGenres.getGenres().size());
    }

    @Test
    void createRejectsNullUser() {
        assertThrows(
                IllegalArgumentException.class,
                () -> Story.create(null, List.of(Genre.FANTASY))
        );
    }

    @Test
    void createRejectsMissingGenres() {
        User user = createUser();

        assertThrows(IllegalArgumentException.class, () -> Story.create(user, null));
        assertThrows(IllegalArgumentException.class, () -> Story.create(user, List.of()));
    }

    @Test
    void createRejectsMoreThanThreeGenres() {
        assertThrows(
                IllegalArgumentException.class,
                () -> Story.create(
                        createUser(),
                        List.of(
                                Genre.FANTASY,
                                Genre.ADVENTURE,
                                Genre.MYSTERY,
                                Genre.HORROR
                        )
                )
        );
    }

    @Test
    void createRejectsDuplicateGenres() {
        assertThrows(
                IllegalArgumentException.class,
                () -> Story.create(
                        createUser(),
                        List.of(Genre.FANTASY, Genre.FANTASY)
                )
        );
    }

    private static User createUser() {
        return User.createLocal("user@example.com", "encoded-password", "storyteller");
    }
}
