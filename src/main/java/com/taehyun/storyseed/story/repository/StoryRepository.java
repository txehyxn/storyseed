package com.taehyun.storyseed.story.repository;

import com.taehyun.storyseed.story.domain.Story;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StoryRepository extends JpaRepository<Story, Long> {

    List<Story> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<Story> findByIdAndUserId(Long storyId, Long userId);
}
