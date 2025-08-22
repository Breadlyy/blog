package com.example.blog;

import com.example.blog.model.Post;
import com.example.blog.repository.PostRepository;
import com.example.blog.service.PostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
@Rollback
class PostServiceIntegrationTest {

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @Test
    void testCreateAndUpdatePost() {
        Post post = postService.createPost("Integration Test Post", null, "#spring #java", "Some blog text");
        assertThat(post.getId()).isNotNull();
        assertThat(post.getTags()).hasSize(2);

        Post updated = postService.updatePost(
                post.getId(),
                "Updated Title",
                null,
                "#spring",
                "Updated text"
        );

        assertThat(updated.getTitle()).isEqualTo("Updated Title");
        assertThat(updated.getText()).isEqualTo("Updated text");
        assertThat(updated.getTags()).hasSize(1);
    }

    @Test
    void testLikePost() {
        Post post = postService.createPost("Like Test", null, "#like", "Some text");
        assertThat(post.getLikes()).isZero();

        postService.likePost(post.getId(), true);
        Post liked = postRepository.findById(post.getId()).orElseThrow();
        assertThat(liked.getLikes()).isEqualTo(1);

        postService.likePost(post.getId(), false);
        Post unliked = postRepository.findById(post.getId()).orElseThrow();
        assertThat(unliked.getLikes()).isEqualTo(0);
    }
}
