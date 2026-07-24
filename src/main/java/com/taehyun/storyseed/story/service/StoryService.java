package com.taehyun.storyseed.story.service;

import com.taehyun.storyseed.story.domain.Story;
import com.taehyun.storyseed.story.domain.Chapter;
import com.taehyun.storyseed.story.domain.Choice;
import com.taehyun.storyseed.story.dto.CreateStoryRequest;
import com.taehyun.storyseed.story.dto.StoryDetailView;
import com.taehyun.storyseed.story.generation.GeneratedChapterResult;
import com.taehyun.storyseed.story.generation.StoryGenerationService;
import com.taehyun.storyseed.story.repository.ChapterRepository;
import com.taehyun.storyseed.story.repository.ChoiceRepository;
import com.taehyun.storyseed.story.repository.StoryRepository;
import com.taehyun.storyseed.user.domain.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class StoryService {

    private final StoryRepository storyRepository;
    private final ChapterRepository chapterRepository;
    private final ChoiceRepository choiceRepository;
    private final StoryGenerationService storyGenerationService;

    public StoryService(
            StoryRepository storyRepository,
            ChapterRepository chapterRepository,
            ChoiceRepository choiceRepository,
            StoryGenerationService storyGenerationService
    ) {
        this.storyRepository = storyRepository;
        this.chapterRepository = chapterRepository;
        this.choiceRepository = choiceRepository;
        this.storyGenerationService = storyGenerationService;
    }

    @Transactional
    public Story createStory(User user, CreateStoryRequest request) {
        if (user == null) {
            throw new IllegalArgumentException("user must not be null");
        }

        Story story = storyRepository.save(Story.create(user, request.genres()));
        GeneratedChapterResult opening = storyGenerationService.generateOpening(story);
        Chapter chapter = chapterRepository.save(Chapter.create(story, 1, opening.content()));

        for (int index = 0; index < opening.choices().size(); index++) {
            choiceRepository.save(Choice.create(chapter, index + 1, opening.choices().get(index)));
        }

        return story;
    }

    public Story getStory(User user, Long storyId) {
        if (user == null) {
            throw new IllegalArgumentException("user must not be null");
        }

        return storyRepository.findByIdAndUserId(storyId, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("story not found"));
    }

    public StoryDetailView getStoryDetail(User user, Long storyId) {
        Story story = getStory(user, storyId);
        java.util.List<StoryDetailView.ChapterView> chapters = chapterRepository
                .findAllByStoryIdOrderByChapterNumberAsc(storyId)
                .stream()
                .map(chapter -> new StoryDetailView.ChapterView(
                        chapter.getChapterNumber(),
                        chapter.getContent(),
                        choiceRepository
                                .findAllByChapterIdOrderByChoiceNumberAsc(chapter.getId())
                                .stream()
                                .map(StoryDetailView.ChoiceView::from)
                                .toList()
                ))
                .toList();

        return StoryDetailView.from(story, chapters);
    }
}
