package com.example.blog.service;

import com.example.blog.exception.PostNotFoundException;
import com.example.blog.model.Post;
import com.example.blog.repository.PostRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final TagService tagService;

    public PostService(PostRepository postRepository, TagService tagService) {
        this.postRepository = postRepository;
        this.tagService = tagService;
    }

    @Transactional
    public Page<Post> findAll(Pageable page) {
        return postRepository.findAll(page);
    }

    @Transactional
    public Post findById(Long id) {
        return postRepository.findById(id).get();
    }

    @CacheEvict(value = "postsByTag", allEntries = true)
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
    @CacheEvict(value = "postsByTag", allEntries = true)
    public Post updatePost(Long id, String title, byte[] image, String tagsText, String text) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException("Post not found with id " + id));
        post.setTitle(title);
        post.setText(text);
        if (image != null && image.length > 0) {
            post.setImage(image);
        }
        post.setTags(tagService.parseTags(tagsText));
        post.setUpdatedAt(LocalDateTime.now());
        return postRepository.save(post);
    }
    @CacheEvict(value = "postsByTag", allEntries = true)
    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }
    @Transactional
    public Post findPostByIdWithCommentsAndTags(Long id)
    {
        return postRepository.findByIdWithCommentsAndTags(id)
                .orElseThrow(() ->  new PostNotFoundException("Post not found with id " + id));
    }

    public void likePost(Long id, boolean like) {
        Post post = findById(id);
        if (like)
            post.setLikes(post.getLikes() + 1);
        else
            post.setLikes(post.getLikes() - 1);
        postRepository.save(post);
    }

    @Transactional
    @Cacheable(value = "postsByTag", key = "#search + '-' + #pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<Post> findByTag(String search, Pageable pageable) {
        return postRepository.findByTagStartingWith(search, pageable);
    }
    public Post save(Post post)
    {
        return postRepository.save(post);
    }
}
