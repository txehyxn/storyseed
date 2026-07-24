package com.taehyun.storyseed.story.controller;

import com.taehyun.storyseed.story.domain.Story;
import com.taehyun.storyseed.story.domain.Genre;
import com.taehyun.storyseed.story.dto.CreateStoryRequest;
import com.taehyun.storyseed.story.service.StoryService;
import com.taehyun.storyseed.user.domain.User;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/stories")
public class StoryController {

    private final StoryService storyService;

    public StoryController(StoryService storyService) {
        this.storyService = storyService;
    }

    @GetMapping("/new")
    public String newStoryForm(Model model) {
        model.addAttribute("request", new CreateStoryRequest(java.util.List.of()));
        model.addAttribute("genres", Genre.values());
        return "story/new";
    }

    @PostMapping
    public String createStory(
            @AuthenticationPrincipal User user,
            @Valid @ModelAttribute("request") CreateStoryRequest request,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("genres", Genre.values());
            return "story/new";
        }

        Story story = storyService.createStory(user, request);
        return "redirect:/stories/" + story.getId();
    }

    @GetMapping("/{storyId}")
    public String storyDetail(
            @AuthenticationPrincipal User user,
            @PathVariable Long storyId,
            Model model
    ) {
        model.addAttribute("story", storyService.getStoryDetail(user, storyId));
        return "story/detail";
    }
}
