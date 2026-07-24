package com.taehyun.storyseed.story.domain;

import com.taehyun.storyseed.user.domain.User;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ChoiceTest {

    @Test
    void createSetsRequiredFields() {
        Chapter chapter = createChapter();

        Choice choice = Choice.create(chapter, 1, "숲으로 들어간다");

        assertSame(chapter, choice.getChapter());
        assertEquals(1, choice.getChoiceNumber());
        assertEquals("숲으로 들어간다", choice.getContent());
    }

    @Test
    void createRejectsNullChapter() {
        assertThrows(
                IllegalArgumentException.class,
                () -> Choice.create(null, 1, "숲으로 들어간다")
        );
    }

    @Test
    void createRejectsChoiceNumberLessThanOne() {
        Chapter chapter = createChapter();

        assertThrows(
                IllegalArgumentException.class,
                () -> Choice.create(chapter, 0, "숲으로 들어간다")
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> Choice.create(chapter, -1, "숲으로 들어간다")
        );
    }

    @Test
    void createTrimsContent() {
        Choice choice = Choice.create(createChapter(), 1, "  숲으로 들어간다  ");

        assertEquals("숲으로 들어간다", choice.getContent());
    }

    @Test
    void createRejectsNullOrBlankContent() {
        Chapter chapter = createChapter();

        assertThrows(
                IllegalArgumentException.class,
                () -> Choice.create(chapter, 1, null)
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> Choice.create(chapter, 1, "   ")
        );
    }

    private static Chapter createChapter() {
        User user = User.createLocal("user@example.com", "encoded-password", "storyteller");
        Story story = Story.create(user, List.of(Genre.ADVENTURE));
        return Chapter.create(story, 1, "이야기의 시작");
    }
}
