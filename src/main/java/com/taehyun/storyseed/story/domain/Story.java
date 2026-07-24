package com.taehyun.storyseed.story.domain;

import com.taehyun.storyseed.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.OrderColumn;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Getter
@Entity
@Table(name = "stories")
public class Story {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(length = 100)
    private String title;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "story_genre",
            joinColumns = @JoinColumn(name = "story_id")
    )
    @OrderColumn(name = "genre_order")
    @Enumerated(EnumType.STRING)
    @Column(name = "genre", nullable = false, length = 30)
    private List<Genre> genres = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StoryStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected Story() {
    }

    private Story(User user, List<Genre> genres) {
        this.user = user;
        this.genres.addAll(genres);
        this.status = StoryStatus.IN_PROGRESS;
    }

    public static Story create(User user, List<Genre> genres) {
        if (user == null) {
            throw new IllegalArgumentException("user must not be null");
        }

        validateGenres(genres);
        return new Story(user, genres);
    }

    public Genre getPrimaryGenre() {
        return genres.getFirst();
    }

    public List<Genre> getSecondaryGenres() {
        return List.copyOf(genres.subList(1, genres.size()));
    }

    public List<Genre> getGenres() {
        return List.copyOf(genres);
    }

    public String getDisplayTitle() {
        return genres.stream()
                .map(Genre::getDisplayName)
                .reduce((left, right) -> left + " · " + right)
                .orElse("") + " 이야기";
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

    private static void validateGenres(List<Genre> genres) {
        if (genres == null || genres.isEmpty()) {
            throw new IllegalArgumentException("at least one genre is required");
        }
        if (genres.size() > 3) {
            throw new IllegalArgumentException("genres must contain at most 3 items");
        }
        if (genres.stream().anyMatch(java.util.Objects::isNull)) {
            throw new IllegalArgumentException("genre must not be null");
        }
        if (new HashSet<>(genres).size() != genres.size()) {
            throw new IllegalArgumentException("genres must not contain duplicates");
        }
    }
}
