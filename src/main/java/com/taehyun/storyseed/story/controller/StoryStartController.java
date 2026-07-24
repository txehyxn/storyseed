package com.taehyun.storyseed.story.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/story")
public class StoryStartController {

    @GetMapping("/start")
    public String start() {
        return "story/start";
    }

    @GetMapping("/coming-soon")
    public String comingSoon(
            @RequestParam(required = false) String mode,
            Model model
    ) {
        model.addAttribute("featureName", resolveFeatureName(mode));
        return "story/coming-soon";
    }

    private String resolveFeatureName(String mode) {
        if (mode == null) {
            return "새로운 이야기 기능";
        }

        return switch (mode) {
            case "classic" -> "명작 다시 쓰기";
            case "world" -> "나만의 세계 만들기";
            case "recommendation" -> "AI 추천 이야기";
            case "seed" -> "이야기 씨앗으로 만들기";
            case "continue" -> "이어서 쓰기";
            default -> "새로운 이야기 기능";
        };
    }
}
