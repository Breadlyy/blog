package com.example.blog;

import com.example.blog.model.Comment;
import com.example.blog.model.Post;
import com.example.blog.repository.CommentRepository;
import com.example.blog.service.CommentService;
import com.example.blog.service.PostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
@Rollback
class CommentServiceIntegrationTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private PostService postService;

    @Autowired
    private CommentRepository commentRepository;

    @Test
    void testAddAndUpdateComment() {
        Post post = postService.createPost("Test title", null, "#tag1", "Some text");
        assertThat(post.getId()).isNotNull();

        Comment comment = commentService.addComment(post.getId(), "Nice post!");
        assertThat(comment.getId()).isNotNull();
        assertThat(comment.getText()).isEqualTo("Nice post!");

        Comment updated = commentService.updateComment(comment.getId(), "Edited comment");
        assertThat(updated.getText()).isEqualTo("Edited comment");

        Optional<Comment> found = commentRepository.findById(comment.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getText()).isEqualTo("Edited comment");
    }

    @Test
    void testDeleteComment() {
        Post post = postService.createPost("Another post", null, "#tag2", "Some text 2");
        Comment comment = commentService.addComment(post.getId(), "To be deleted");

        Long id = comment.getId();
        commentService.deleteComment(id);

        assertThat(commentRepository.findById(id)).isEmpty();
    }
}
