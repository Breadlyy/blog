package com.example.blog.controller;

import com.example.blog.model.Comment;
import com.example.blog.model.Post;
import com.example.blog.service.CommentService;
import com.example.blog.service.PostService;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("/")
public class MainController {

    private final PostService postService;
    private final CommentService commentService;

    public MainController(PostService postService, CommentService commentService) {
        this.postService = postService;
        this.commentService = commentService;
    }

    @GetMapping("/posts")
    public String getAllPosts(Model model) {
        List<Post> posts = postService.findAll();
        model.addAttribute("posts", posts);
        return "posts";
    }


    @GetMapping("/posts/{id}")
    public String getPostById(@PathVariable Long id, Model model) {
        return postService.findById(id).map(post -> {
            model.addAttribute("post", post);
            List<Comment> comments = commentService.findCommentsByPostId(id);
            model.addAttribute("comments", comments);
            return "post";
        }).orElse("error/404");
    }

    @GetMapping("/posts/new")
    public String showCreatePostForm() {
        return "add-post";
    }


    @PostMapping("/posts")
    public String createPost(
            @RequestParam String title,
            @RequestParam(required = false) MultipartFile image,
            @RequestParam String tags,
            @RequestParam String text,
            Model model) throws IOException {

        byte[] imageBytes = (image != null) ? image.getBytes() : null;
        Post createdPost = postService.createPost(title, imageBytes, tags, text);
        return "redirect:/posts/" + createdPost.getId();
    }

    @GetMapping("/posts/{id}/edit")
    public String showEditPostForm(@PathVariable Long id, Model model) {
        return postService.findById(id).map(post -> {
            model.addAttribute("post", post);
            return "add-post";
        }).orElse("error/404");
    }

    @PostMapping("/posts/{id}/edit")
    public String updatePost(
            @PathVariable Long id,
            @RequestParam String title,
            @RequestParam(required = false) MultipartFile image,
            @RequestParam String tags,
            @RequestParam String text,
            Model model) throws IOException {

        byte[] imageBytes = (image != null) ? image.getBytes() : null;
        try {
            postService.updatePost(id, title, imageBytes, tags, text);
            return "redirect:/posts/" + id;
        } catch (NoSuchElementException e) {
            return "error/404";
        }
    }

    @PostMapping("/posts/{id}/delete")
    public String deletePost(@PathVariable Long id) {
        try {
            postService.deletePost(id);
            return "redirect:/posts";
        } catch (EmptyResultDataAccessException e) {
            return "error/404";
        }
    }

    @PostMapping("/posts/{postId}/comments")
    public String addComment(
            @PathVariable Long postId,
            @RequestParam String text) {
        try {
            commentService.addComment(postId, text);
            return "redirect:/posts/" + postId;
        } catch (NoSuchElementException e) {
            return "error/404";
        }
    }

    @PostMapping("/comments/{id}/delete")
    public String deleteComment(@PathVariable Long id, @RequestParam Long postId) {
        try {
            commentService.deleteComment(id);
            return "redirect:/posts/" + postId;
        } catch (EmptyResultDataAccessException e) {
            return "error/404";
        }
    }

}
