package com.taehyun.storyseed.story.service;

import com.taehyun.storyseed.story.domain.Story;
import com.taehyun.storyseed.story.dto.CreateStoryRequest;
import com.taehyun.storyseed.story.repository.StoryRepository;
import com.taehyun.storyseed.user.domain.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class StoryService {

    private final StoryRepository storyRepository;

    public StoryService(StoryRepository storyRepository) {
        this.storyRepository = storyRepository;
    }

    @Transactional
    public Story createStory(User user, CreateStoryRequest request) {
        if (user == null) {
            throw new IllegalArgumentException("user must not be null");
        }

        Story story = Story.create(user, request.title(), request.theme());
        return storyRepository.save(story);
    }

    public Story getStory(User user, Long storyId) {
        if (user == null) {
            throw new IllegalArgumentException("user must not be null");
        }

        return storyRepository.findByIdAndUserId(storyId, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("story not found"));
    }
}
