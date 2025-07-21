package com.example.blog.service;

import com.example.blog.model.Tag;
import com.example.blog.repository.TagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TagServiceTest {

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private TagService tagService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testParseTagsWithExistingTags() {
        String tagsText = "tag1, tag2";
        Tag tag1 = new Tag(1L, "tag1", new HashSet<>());
        Tag tag2 = new Tag(2L, "tag2", new HashSet<>());

        when(tagRepository.findByName("tag1")).thenReturn(Optional.of(tag1));
        when(tagRepository.findByName("tag2")).thenReturn(Optional.of(tag2));

        Set<Tag> tags = tagService.parseTags(tagsText);

        assertEquals(2, tags.size());
        assertTrue(tags.contains(tag1));
        assertTrue(tags.contains(tag2));
        verify(tagRepository, times(1)).findByName("tag1");
        verify(tagRepository, times(1)).findByName("tag2");
    }

    @Test
    public void testParseTagsWithNewTags() {
        String tagsText = "tag1, tag2";
        when(tagRepository.findByName("tag1")).thenReturn(Optional.empty());
        when(tagRepository.findByName("tag2")).thenReturn(Optional.empty());
        when(tagRepository.save(any(Tag.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Set<Tag> tags = tagService.parseTags(tagsText);

        assertEquals(2, tags.size());
        assertTrue(tags.stream().anyMatch(tag -> tag.getName().equals("tag1")));
        assertTrue(tags.stream().anyMatch(tag -> tag.getName().equals("tag2")));
        verify(tagRepository, times(1)).findByName("tag1");
        verify(tagRepository, times(1)).findByName("tag2");
        verify(tagRepository, times(2)).save(any(Tag.class));
    }

    @Test
    public void testParseTagsWithEmptyInput() {
        String tagsText = "";

        Set<Tag> tags = tagService.parseTags(tagsText);

        assertTrue(tags.isEmpty());
        verify(tagRepository, never()).findByName(anyString());
    }

    @Test
    public void testParseTagsWithNullInput() {
        String tagsText = null;

        Set<Tag> tags = tagService.parseTags(tagsText);

        assertTrue(tags.isEmpty());
        verify(tagRepository, never()).findByName(anyString());
    }
}
