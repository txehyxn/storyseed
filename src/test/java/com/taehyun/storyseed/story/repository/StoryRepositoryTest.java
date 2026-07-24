package com.taehyun.storyseed.story.repository;

import com.taehyun.storyseed.story.domain.Genre;
import com.taehyun.storyseed.story.domain.Story;
import com.taehyun.storyseed.user.domain.User;
import com.taehyun.storyseed.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class StoryRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StoryRepository storyRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void genreSelectionOrderIsPreservedAfterReload() {
        User user = userRepository.save(
                User.createLocal("genre@test.com", "encoded-password", "genre-user")
        );
        Story story = storyRepository.save(
                Story.create(
                        user,
                        List.of(Genre.FANTASY, Genre.MYSTERY, Genre.HORROR)
                )
        );
        entityManager.flush();
        entityManager.clear();

        Story reloaded = storyRepository.findById(story.getId()).orElseThrow();

        assertEquals(
                List.of(Genre.FANTASY, Genre.MYSTERY, Genre.HORROR),
                reloaded.getGenres()
        );
        assertEquals(Genre.FANTASY, reloaded.getPrimaryGenre());
    }
}
