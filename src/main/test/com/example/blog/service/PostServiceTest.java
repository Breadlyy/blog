package com.example.blog.service;

import com.example.blog.exception.PostNotFoundException;
import com.example.blog.model.Post;
import com.example.blog.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private TagService tagService;

    @InjectMocks
    private PostService postService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreatePost() {
        String title = "Test Title";
        String tagsText = "tag1, tag2";
        String text = "Test text";
        byte[] image = new byte[]{1, 2, 3};

        Post post = new Post();
        post.setTitle(title);
        post.setText(text);
        post.setImage(image);
        post.setLikes(0);
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());

        when(tagService.parseTags(tagsText)).thenReturn(null);
        when(postRepository.save(any(Post.class))).thenReturn(post);


        Post createdPost = postService.createPost(title, image, tagsText, text);

        assertNotNull(createdPost);
        assertEquals(title, createdPost.getTitle());
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    public void testFindById() {
        Long postId = 1L;
        Post post = new Post();
        post.setId(postId);
        post.setTitle("Test Title");

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        Post foundPost = postService.findById(postId);

        assertNotNull(foundPost);
        assertEquals(postId, foundPost.getId());
        verify(postRepository, times(1)).findById(postId);
    }

    @Test
    public void testFindByIdNotFound() {
        Long postId = 1L;
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(PostNotFoundException.class, () -> postService.findById(postId));
    }

    @Test
    public void testUpdatePost() {
        Long postId = 1L;
        Post existingPost = new Post();
        existingPost.setId(postId);
        existingPost.setTitle("Old Title");

        when(postRepository.findById(postId)).thenReturn(Optional.of(existingPost));
        when(postRepository.save(any(Post.class))).thenReturn(existingPost);

        Post updatedPost = postService.updatePost(postId, "New Title", null, "tag1", "New text");

        assertNotNull(updatedPost);
        assertEquals("New Title", updatedPost.getTitle());
        verify(postRepository, times(1)).save(existingPost);
    }

    @Test
    public void testDeletePost() {
        Long postId = 1L;
        doNothing().when(postRepository).deleteById(postId);

        postService.deletePost(postId);

        verify(postRepository, times(1)).deleteById(postId);
    }
}
   