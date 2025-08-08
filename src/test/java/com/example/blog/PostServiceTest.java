package com.example.blog;

import com.example.blog.exception.PostNotFoundException;
import com.example.blog.model.Post;
import com.example.blog.repository.PostRepository;
import com.example.blog.service.PostService;
import com.example.blog.service.TagService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PostServiceTest {

    private PostRepository postRepository;
    private TagService tagService;
    private PostService postService;

    @BeforeEach
    void setUp() {
        postRepository = mock(PostRepository.class);
        tagService = mock(TagService.class);
        postService = new PostService(postRepository, tagService);
    }

    @Test
    void createPost_ShouldSavePost() {
        Post post = new Post();
        post.setTitle("New Post");
        post.setText("Post content");

        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Post createdPost = postService.createPost(post.getTitle(), null, null, post.getText());

        assertNotNull(createdPost);
        assertEquals(post.getTitle(), createdPost.getTitle());
        verify(postRepository).save(any(Post.class));
    }

    @Test
    void updatePost_ShouldUpdatePost_WhenPostExists() {
        Long postId = 1L;
        Post existingPost = new Post();
        existingPost.setId(postId);
        existingPost.setTitle("Old Title");

        when(postRepository.findById(postId)).thenReturn(Optional.of(existingPost));
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(tagService.parseTags(any(String.class))).thenReturn(Collections.emptySet()); // Мокируем метод parseTags

        Post updatedPost = postService.updatePost(postId, "New Title", null, null, "New Content");

        assertEquals("New Title", updatedPost.getTitle());
        verify(postRepository).save(existingPost);
    }

    @Test
    void updatePost_ShouldThrowPostNotFoundException_WhenPostDoesNotExist() {
        Long postId = 1L;

        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(PostNotFoundException.class, () -> postService.updatePost(postId, "New Title", null, null, "New Content"));
    }

    @Test
    void deletePost_ShouldDeletePost_WhenPostExists() {
        Long postId = 1L;

        doNothing().when(postRepository).deleteById(postId);

        postService.deletePost(postId);

        verify(postRepository).deleteById(postId);
    }
}
