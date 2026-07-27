package com.taehyun.storyseed.story.service;

import com.taehyun.storyseed.story.domain.Genre;
import com.taehyun.storyseed.story.domain.Story;
import com.taehyun.storyseed.story.dto.CreateStorySeedRequest;
import com.taehyun.storyseed.story.dto.StoryDetailView;
import com.taehyun.storyseed.story.repository.ChapterRepository;
import com.taehyun.storyseed.story.repository.ChoiceRepository;
import com.taehyun.storyseed.story.repository.StoryRepository;
import com.taehyun.storyseed.user.domain.User;
import com.taehyun.storyseed.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class StorySeedIntegrationTest {

    @Autowired
    private StoryService storyService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StoryRepository storyRepository;

    @Autowired
    private ChapterRepository chapterRepository;

    @Autowired
    private ChoiceRepository choiceRepository;

    @BeforeEach
    void setUp() {
        choiceRepository.deleteAll();
        chapterRepository.deleteAll();
        storyRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void storySeedPersistsStoryOpeningChoicesAndCanBeReadAsDetail() {
        User user = userRepository.save(
                User.createLocal("seed@example.com", "encoded-password", "seed-writer")
        );

        Story story = storyService.createStorySeedStory(
                user,
                new CreateStorySeedRequest("비가 멈추지 않는 도시", Genre.MYSTERY)
        );
        StoryDetailView detail = storyService.getStoryDetail(user, story.getId());

        assertEquals("비가 멈추지 않는 도시: 마지막 우산", detail.title());
        assertEquals(1, detail.chapters().size());
        assertTrue(detail.chapters().get(0).content().contains("비가 멈추지 않는 도시"));
        assertEquals(2, detail.chapters().get(0).choices().size());
    }
}
