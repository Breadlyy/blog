package com.example.blog;

import com.example.blog.exception.CommentNotFoundException;
import com.example.blog.exception.PostNotFoundException;
import com.example.blog.model.Comment;
import com.example.blog.model.Post;
import com.example.blog.repository.CommentRepository;
import com.example.blog.repository.PostRepository;
import com.example.blog.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CommentServiceTest {

    private CommentRepository commentRepository;
    private PostRepository postRepository;
    private CommentService commentService;

    @BeforeEach
    void setUp() {
        commentRepository = mock(CommentRepository.class);
        postRepository = mock(PostRepository.class);
        commentService = new CommentService(commentRepository, postRepository);
    }

    @Test
    void addComment_ShouldSaveComment_WhenPostExists() {
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
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void addComment_ShouldThrowPostNotFoundException_WhenPostDoesNotExist() {
        Long postId = 1L;
        String commentText = "This is a comment";

        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(PostNotFoundException.class, () -> commentService.addComment(postId, commentText));
    }

    @Test
    void updateComment_ShouldUpdateComment_WhenCommentExists() {
        Long commentId = 1L;
        String newText = "Updated comment";
        Comment existingComment = new Comment();
        existingComment.setId(commentId);
        existingComment.setText("Old comment");

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(existingComment));
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Comment updatedComment = commentService.updateComment(commentId, newText);

        assertEquals(newText, updatedComment.getText());
        verify(commentRepository).save(existingComment);
    }

    @Test
    void updateComment_ShouldThrowCommentNotFoundException_WhenCommentDoesNotExist() {
        Long commentId = 1L;
        String newText = "Updated comment";

        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        assertThrows(CommentNotFoundException.class, () -> commentService.updateComment(commentId, newText));
    }

    @Test
    void deleteComment_ShouldDeleteComment_WhenCommentExists() {
        Long commentId = 1L;

        doNothing().when(commentRepository).deleteById(commentId);

        commentService.deleteComment(commentId);

        verify(commentRepository).deleteById(commentId);
    }
}
