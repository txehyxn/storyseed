package com.taehyun.storyseed.story.service;

import com.taehyun.storyseed.story.domain.Story;
import com.taehyun.storyseed.story.domain.Chapter;
import com.taehyun.storyseed.story.domain.Choice;
import com.taehyun.storyseed.story.dto.CreateStoryRequest;
import com.taehyun.storyseed.story.dto.CreateCustomWorldRequest;
import com.taehyun.storyseed.story.dto.CreateAiRecommendationRequest;
import com.taehyun.storyseed.story.dto.CreateStorySeedRequest;
import com.taehyun.storyseed.story.dto.StoryDetailView;
import com.taehyun.storyseed.story.generation.GeneratedStoryResult;
import com.taehyun.storyseed.story.generation.StoryGenerationRequest;
import com.taehyun.storyseed.story.generation.StoryGenerationService;
import com.taehyun.storyseed.story.generation.RecommendationMood;
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
        validateUser(user);
        Story story = storyRepository.save(Story.create(user, request.genres()));
        GeneratedStoryResult generatedStory = storyGenerationService.generate(
                StoryGenerationRequest.genre(request.genres())
        );
        saveOpening(story, generatedStory.content(), generatedStory.choices());
        return story;
    }

    @Transactional
    public Story createClassicRemakeStory(
            User user,
            CreateStoryRequest request,
            String classicTitle,
            String remakeType
    ) {
        validateUser(user);
        Story story = storyRepository.save(Story.create(user, request.genres()));
        GeneratedStoryResult generatedStory = storyGenerationService.generate(
                StoryGenerationRequest.classicRemake(
                        request.genres(),
                        classicTitle,
                        remakeType
                )
        );
        storyRepository.updateTitle(story.getId(), generatedStory.title());
        saveOpening(story, generatedStory.content(), generatedStory.choices());
        return story;
    }

    @Transactional
    public Story createCustomWorldStory(
            User user,
            CreateCustomWorldRequest request
    ) {
        validateUser(user);
        java.util.List<com.taehyun.storyseed.story.domain.Genre> genres =
                java.util.List.of(request.worldTheme().getGenre());
        Story story = storyRepository.save(Story.create(user, genres));
        GeneratedStoryResult generatedStory = storyGenerationService.generate(
                StoryGenerationRequest.customWorld(
                        genres,
                        request.worldName(),
                        request.worldTheme(),
                        request.worldEra(),
                        request.protagonistName()
                )
        );
        storyRepository.updateTitle(story.getId(), generatedStory.title());
        saveOpening(story, generatedStory.content(), generatedStory.choices());
        return story;
    }

    @Transactional
    public Story createAiRecommendationStory(
            User user,
            CreateAiRecommendationRequest request
    ) {
        validateUser(user);
        java.util.List<com.taehyun.storyseed.story.domain.Genre> genres =
                java.util.List.of(resolveRecommendationGenre(request.mood()));
        Story story = storyRepository.save(Story.create(user, genres));
        GeneratedStoryResult generatedStory = storyGenerationService.generate(
                StoryGenerationRequest.aiRecommendation(
                        genres,
                        request.mood(),
                        request.pacing(),
                        request.protagonistType()
                )
        );
        storyRepository.updateTitle(story.getId(), generatedStory.title());
        saveOpening(story, generatedStory.content(), generatedStory.choices());
        return story;
    }

    @Transactional
    public Story createStorySeedStory(
            User user,
            CreateStorySeedRequest request
    ) {
        validateUser(user);
        java.util.List<com.taehyun.storyseed.story.domain.Genre> genres =
                java.util.List.of(request.genre());
        Story story = storyRepository.save(Story.create(user, genres));
        GeneratedStoryResult generatedStory = storyGenerationService.generate(
                StoryGenerationRequest.storySeed(genres, request.seedText())
        );
        storyRepository.updateTitle(story.getId(), generatedStory.title());
        saveOpening(story, generatedStory.content(), generatedStory.choices());
        return story;
    }

    private com.taehyun.storyseed.story.domain.Genre resolveRecommendationGenre(
            RecommendationMood mood
    ) {
        return switch (mood) {
            case WARM -> com.taehyun.storyseed.story.domain.Genre.SLICE_OF_LIFE;
            case MYSTERIOUS -> com.taehyun.storyseed.story.domain.Genre.FANTASY;
            case TENSE -> com.taehyun.storyseed.story.domain.Genre.MYSTERY;
            case CHEERFUL -> com.taehyun.storyseed.story.domain.Genre.COMEDY;
            case MOVING -> com.taehyun.storyseed.story.domain.Genre.ROMANCE;
        };
    }

    private void saveOpening(
            Story story,
            String content,
            java.util.List<String> choices
    ) {
        Chapter chapter = chapterRepository.save(Chapter.create(story, 1, content));

        for (int index = 0; index < choices.size(); index++) {
            choiceRepository.save(Choice.create(chapter, index + 1, choices.get(index)));
        }
    }

    private void validateUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("user must not be null");
        }
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
