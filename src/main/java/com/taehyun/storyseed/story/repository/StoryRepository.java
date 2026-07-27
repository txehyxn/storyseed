package com.taehyun.storyseed.story.repository;

import com.taehyun.storyseed.story.domain.Story;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StoryRepository extends JpaRepository<Story, Long> {

    List<Story> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<Story> findByIdAndUserId(Long storyId, Long userId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("update Story story set story.title = :title where story.id = :storyId")
    void updateTitle(
            @Param("storyId") Long storyId,
            @Param("title") String title
    );
}
