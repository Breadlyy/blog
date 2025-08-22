package com.example.blog;

import com.example.blog.model.Tag;
import com.example.blog.repository.TagRepository;
import com.example.blog.service.TagService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TagServiceTest {

    private TagRepository tagRepository;
    private TagService tagService;

    @BeforeEach
    void setUp() {
        tagRepository = mock(TagRepository.class);
        tagService = new TagService(tagRepository);
    }

    @Test
    void parseTags_ShouldReturnEmptySet_WhenTagsTextIsNull() {
        Set<Tag> tags = tagService.parseTags(null);
        assertTrue(tags.isEmpty());
    }

    @Test
    void parseTags_ShouldReturnTags_WhenTagsTextIsProvided() {
        String tagsText = "tag1, tag2";
        Tag tag1 = new Tag(null, "tag1", Collections.emptySet());
        Tag tag2 = new Tag(null, "tag2", Collections.emptySet());

        when(tagRepository.findByName("tag1")).thenReturn(Optional.empty());
        when(tagRepository.findByName("tag2")).thenReturn(Optional.empty());
        when(tagRepository.save(any(Tag.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Set<Tag> tags = tagService.parseTags(tagsText);

        assertEquals(2, tags.size());
        assertTrue(tags.contains(tag1));
        assertTrue(tags.contains(tag2));
        verify(tagRepository).save(tag1);
        verify(tagRepository).save(tag2);
    }
}

