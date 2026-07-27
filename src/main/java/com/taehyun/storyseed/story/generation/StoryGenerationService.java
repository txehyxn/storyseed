package com.taehyun.storyseed.story.generation;

import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
public class StoryGenerationService {

    private final Map<StoryGenerationMode, StoryGenerator> generators;

    public StoryGenerationService(List<StoryGenerator> generators) {
        EnumMap<StoryGenerationMode, StoryGenerator> registry =
                new EnumMap<>(StoryGenerationMode.class);

        for (StoryGenerator generator : generators) {
            StoryGenerator duplicate = registry.put(generator.mode(), generator);
            if (duplicate != null) {
                throw new IllegalStateException(
                        "duplicate story generator: " + generator.mode()
                );
            }
        }

        this.generators = Map.copyOf(registry);
    }

    public GeneratedStoryResult generate(StoryGenerationRequest request) {
        StoryGenerator generator = generators.get(request.mode());
        if (generator == null) {
            throw new IllegalArgumentException(
                    "unsupported story generation mode: " + request.mode()
            );
        }

        return generator.generate(request);
    }
}
