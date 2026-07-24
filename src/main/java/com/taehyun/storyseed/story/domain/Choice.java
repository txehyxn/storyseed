package com.taehyun.storyseed.story.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
        name = "choices",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_choices_chapter_id_choice_number",
                columnNames = {"chapter_id", "choice_number"}
        )
)
public class Choice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id", nullable = false)
    private Chapter chapter;

    @Column(name = "choice_number", nullable = false)
    private Integer choiceNumber;

    @Column(nullable = false, length = 500)
    private String content;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected Choice() {
    }

    private Choice(Chapter chapter, int choiceNumber, String content) {
        this.chapter = chapter;
        this.choiceNumber = choiceNumber;
        this.content = content;
    }

    public static Choice create(Chapter chapter, int choiceNumber, String content) {
        if (chapter == null) {
            throw new IllegalArgumentException("chapter must not be null");
        }
        if (choiceNumber < 1) {
            throw new IllegalArgumentException("choiceNumber must be at least 1");
        }

        return new Choice(chapter, choiceNumber, normalizeRequired(content));
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
