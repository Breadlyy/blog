package com.example.blog.service;

import com.example.blog.exception.CommentNotFoundException;
import com.example.blog.exception.PostNotFoundException;
import com.example.blog.model.Comment;
import com.example.blog.model.Post;
import com.example.blog.repository.CommentRepository;
import com.example.blog.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private CommentService commentService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddComment() {
        Long postId = 1L;
        String commentText = "This is a comment";
        Post post = new Post();
        post.setId(postId);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Comment comment = commentService.addComment(postId, commentText);

        assertNotNull(comment);
        assertEquals(commentText, comment.getText());
        assertEquals(post, comment.getPost());
        verify(postRepository, times(1)).findById(postId);
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    public void testAddCommentPostNotFound() {
        Long postId = 1L;
        String commentText = "This is a comment";

        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(PostNotFoundException.class, () -> commentService.addComment(postId, commentText));
    }

    @Test
    public void testUpdateComment() {
        Long commentId = 1L;
        String newText = "Updated comment text";
        Comment existingComment = new Comment();
        existingComment.setId(commentId);
        existingComment.setText("Old comment text");

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(existingComment));
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Comment updatedComment = commentService.updateComment(commentId, newText);

        assertNotNull(updatedComment);
        assertEquals(newText, updatedComment.getText());
        verify(commentRepository, times(1)).findById(commentId);
        verify(commentRepository, times(1)).save(existingComment);
    }

    @Test
    public void testUpdateCommentNotFound() {
        Long commentId = 1L;
        String newText = "Updated comment text";

        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        assertThrows(CommentNotFoundException.class, () -> commentService.updateComment(commentId, newText));
    }

    @Test
    public void testDeleteComment() {
        Long commentId = 1L;
        doNothing().when(commentRepository).deleteById(commentId);

        commentService.deleteComment(commentId);

        verify(commentRepository, times(1)).deleteById(commentId);
    }

    @Test
    public void testEditComment() {
        Long commentId = 1L;
        String newText = "Edited comment text";
        Comment existingComment = new Comment();
        existingComment.setId(commentId);
        existingComment.setText("Old comment text");

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(existingComment));
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        commentService.editComment(commentId, newText);

        assertEquals(newText, existingComment.getText());
        verify(commentRepository, times(1)).findById(commentId);
        verify(commentRepository, times(1)).save(existingComment);
    }

    @Test
    public void testEditCommentNotFound() {
        Long commentId = 1L;
        String newText = "Edited comment text";

        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        assertThrows(CommentNotFoundException.class, () -> commentService.editComment(commentId, newText));
    }
}
