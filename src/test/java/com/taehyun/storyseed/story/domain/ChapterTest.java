package com.taehyun.storyseed.story.domain;

import com.taehyun.storyseed.user.domain.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ChapterTest {

    @Test
    void createSetsRequiredFields() {
        Story story = createStory();

        Chapter chapter = Chapter.create(story, 1, "이야기의 시작");

        assertSame(story, chapter.getStory());
        assertEquals(1, chapter.getChapterNumber());
        assertEquals("이야기의 시작", chapter.getContent());
    }

    @Test
    void createRejectsNullStory() {
        assertThrows(
                IllegalArgumentException.class,
                () -> Chapter.create(null, 1, "이야기의 시작")
        );
    }

    @Test
    void createRejectsChapterNumberLessThanOne() {
        Story story = createStory();

        assertThrows(
                IllegalArgumentException.class,
                () -> Chapter.create(story, 0, "이야기의 시작")
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> Chapter.create(story, -1, "이야기의 시작")
        );
    }

    @Test
    void createTrimsContent() {
        Chapter chapter = Chapter.create(createStory(), 1, "  이야기의 시작  ");

        assertEquals("이야기의 시작", chapter.getContent());
    }

    @Test
    void createRejectsNullOrBlankContent() {
        Story story = createStory();

        assertThrows(
                IllegalArgumentException.class,
                () -> Chapter.create(story, 1, null)
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> Chapter.create(story, 1, "   ")
        );
    }

    private static Story createStory() {
        User user = User.createLocal("user@example.com", "encoded-password", "storyteller");
        return Story.create(user, "모험 이야기", "잃어버린 왕국 탐험");
    }
}
