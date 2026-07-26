package com.taehyun.storyseed.story.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/story")
public class StoryStartController {

    private static final List<ClassicStory> CLASSIC_STORIES = List.of(
            new ClassicStory("heungbu-nolbu", "흥부와 놀부", "욕심과 나눔을 다룬 대표적인 고전 이야기"),
            new ClassicStory("kongjwi-patjwi", "콩쥐팥쥐", "어려움 속에서도 희망을 잃지 않는 자매의 이야기"),
            new ClassicStory("honggildong", "홍길동전", "새로운 세상을 꿈꾼 영웅 홍길동의 모험"),
            new ClassicStory("simcheong", "심청전", "효심과 희생으로 기적을 만든 심청의 이야기"),
            new ClassicStory("fairy-woodcutter", "선녀와 나무꾼", "하늘과 땅을 잇는 사랑과 약속의 이야기"),
            new ClassicStory("byeoljubujeon", "별주부전", "지혜로운 토끼와 충직한 자라의 바닷속 모험"),
            new ClassicStory("tortoise-hare", "토끼와 거북이", "꾸준함의 가치를 전하는 우화"),
            new ClassicStory("little-red-riding-hood", "빨간모자", "숲속에서 펼쳐지는 용기와 경계의 이야기"),
            new ClassicStory("snow-white", "백설공주", "질투를 이겨 내고 새로운 가족을 만나는 이야기"),
            new ClassicStory("cinderella", "신데렐라", "용기와 친절로 운명을 바꾼 소녀의 이야기")
    );

    @GetMapping("/start")
    public String start() {
        return "story/start";
    }

    @GetMapping("/classics")
    public String showClassicStories(Model model) {
        model.addAttribute("classicStories", CLASSIC_STORIES);
        return "story/classics";
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
            case "classic", "classic-remake" -> "명작 다시 쓰기";
            case "world" -> "나만의 세계 만들기";
            case "recommendation" -> "AI 추천 이야기";
            case "seed" -> "이야기 씨앗으로 만들기";
            case "continue" -> "이어서 쓰기";
            default -> "새로운 이야기 기능";
        };
    }

    public record ClassicStory(String id, String title, String description) {
    }
}
