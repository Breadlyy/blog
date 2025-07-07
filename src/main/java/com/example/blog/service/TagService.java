package com.example.blog.service;

import com.example.blog.model.Comment;
import com.example.blog.model.Tag;
import com.example.blog.repository.TagRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TagService {

    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public Set<Tag> parseTags(String tagsText) {
        if (tagsText == null || tagsText.isBlank()) {
            return Collections.emptySet();
        }

        String[] parts = tagsText.toLowerCase().replace("#", "").split("[,\\s]+");
        Set<Tag> tags = new HashSet<>();
        for (String name : parts) {
            if (name.isBlank()) continue;
            Tag tag = tagRepository.findByName(name)
                    .orElseGet(() -> tagRepository.save(new Tag(null, name, new HashSet<>())));
            tags.add(tag);
        }
        return tags;
    }
}

