package com.example.blog.service;

import com.example.blog.model.Post;
import com.example.blog.model.Tag;
import com.example.blog.repository.PostRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final TagService tagService;

    public PostService(PostRepository postRepository, TagService tagService) {
        this.postRepository = postRepository;
        this.tagService = tagService;
    }

    public List<Post> findAll() {
        return postRepository.findAll();
    }

    public Optional<Post> findById(Long id) {
        return postRepository.findById(id);
    }

    public Post createPost(String title, byte[] image, String tagsText, String text) {
        Post post = new Post();
        post.setTitle(title);
        post.setText(text);
        post.setImage(image);
        post.setLikes(0);
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        post.setTags(tagService.parseTags(tagsText));
        return postRepository.save(post);
    }

    public Post updatePost(Long id, String title, byte[] image, String tagsText, String text) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Post not found with id " + id));
        post.setTitle(title);
        post.setText(text);
        if (image != null && image.length > 0) {
            post.setImage(image);
        }
        post.setTags(tagService.parseTags(tagsText));
        post.setUpdatedAt(LocalDateTime.now());
        return postRepository.save(post);
    }

    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }

}
