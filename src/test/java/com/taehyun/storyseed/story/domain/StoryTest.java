package com.taehyun.storyseed.story.domain;

import com.taehyun.storyseed.user.domain.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StoryTest {

    @Test
    void createSetsRequiredFieldsAndDefaultStatus() {
        User user = createUser();

        Story story = Story.create(user, "모험 이야기", "잃어버린 왕국 탐험");

        assertSame(user, story.getUser());
        assertEquals("모험 이야기", story.getTitle());
        assertEquals("잃어버린 왕국 탐험", story.getTheme());
        assertEquals(StoryStatus.IN_PROGRESS, story.getStatus());
    }

    @Test
    void completeChangesStatusToCompleted() {
        Story story = createStory();

        story.complete();

        assertEquals(StoryStatus.COMPLETED, story.getStatus());
    }

    @Test
    void createTrimsTitleAndTheme() {
        Story story = Story.create(createUser(), "  모험 이야기  ", "  잃어버린 왕국 탐험  ");

        assertEquals("모험 이야기", story.getTitle());
        assertEquals("잃어버린 왕국 탐험", story.getTheme());
    }

    @Test
    void createRejectsNullUser() {
        assertThrows(
                IllegalArgumentException.class,
                () -> Story.create(null, "모험 이야기", "잃어버린 왕국 탐험")
        );
    }

    @Test
    void createRejectsNullOrBlankTitle() {
        User user = createUser();

        assertThrows(
                IllegalArgumentException.class,
                () -> Story.create(user, null, "잃어버린 왕국 탐험")
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> Story.create(user, "   ", "잃어버린 왕국 탐험")
        );
    }

    @Test
    void createRejectsNullOrBlankTheme() {
        User user = createUser();

        assertThrows(
                IllegalArgumentException.class,
                () -> Story.create(user, "모험 이야기", null)
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> Story.create(user, "모험 이야기", "   ")
        );
    }

    private static Story createStory() {
        return Story.create(createUser(), "모험 이야기", "잃어버린 왕국 탐험");
    }

    private static User createUser() {
        return User.createLocal("user@example.com", "encoded-password", "storyteller");
    }
}
