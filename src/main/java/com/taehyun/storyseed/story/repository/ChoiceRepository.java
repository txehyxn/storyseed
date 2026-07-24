package com.taehyun.storyseed.story.repository;

import com.taehyun.storyseed.story.domain.Choice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChoiceRepository extends JpaRepository<Choice, Long> {

    List<Choice> findAllByChapterIdOrderByChoiceNumberAsc(Long chapterId);
}
