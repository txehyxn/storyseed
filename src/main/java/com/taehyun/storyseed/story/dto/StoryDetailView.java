package com.taehyun.storyseed.story.dto;

import com.taehyun.storyseed.story.domain.Choice;
import com.taehyun.storyseed.story.domain.Genre;
import com.taehyun.storyseed.story.domain.Story;
import com.taehyun.storyseed.story.domain.StoryStatus;

import java.util.List;

public record StoryDetailView(
        Long id,
        String title,
        String displayTitle,
        StoryStatus status,
        Genre primaryGenre,
        List<Genre> secondaryGenres,
        List<ChapterView> chapters
) {

    public static StoryDetailView from(Story story, List<ChapterView> chapters) {
        return new StoryDetailView(
                story.getId(),
                story.getTitle(),
                story.getDisplayTitle(),
                story.getStatus(),
                story.getPrimaryGenre(),
                story.getSecondaryGenres(),
                chapters
        );
    }

    public record ChapterView(
            Integer chapterNumber,
            String content,
            List<ChoiceView> choices
    ) {
    }

    public record ChoiceView(
            Long id,
            Integer choiceNumber,
            String content
    ) {

        public static ChoiceView from(Choice choice) {
            return new ChoiceView(
                    choice.getId(),
                    choice.getChoiceNumber(),
                    choice.getContent()
            );
        }
    }
}
