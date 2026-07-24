package com.taehyun.storyseed.story.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(
        name = "chapters",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_chapters_story_id_chapter_number",
                columnNames = {"story_id", "chapter_number"}
        )
)
public class Chapter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "story_id", nullable = false)
    private Story story;

    @Column(name = "chapter_number", nullable = false)
    private Integer chapterNumber;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected Chapter() {
    }

    private Chapter(Story story, int chapterNumber, String content) {
        this.story = story;
        this.chapterNumber = chapterNumber;
        this.content = content;
    }

    public static Chapter create(Story story, int chapterNumber, String content) {
        if (story == null) {
            throw new IllegalArgumentException("story must not be null");
        }
        if (chapterNumber < 1) {
            throw new IllegalArgumentException("chapterNumber must be at least 1");
        }

        return new Chapter(story, chapterNumber, normalizeRequired(content));
    }

    @PrePersist
    private void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    private void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    private static String normalizeRequired(String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("content must not be blank");
        }
        return content.trim();
    }
}
