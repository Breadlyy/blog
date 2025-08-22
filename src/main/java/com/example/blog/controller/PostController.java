package com.example.blog.controller;

import com.example.blog.model.Post;
import com.example.blog.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    @GetMapping("/")
    public String getAllPosts(
            @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "search", required = false) String search,
            Model model
    ) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Post> page;
        if (search != null && !search.isEmpty()) {
            page = postService.findByTag(search, pageable);
        }
        else {
            page = postService.findAll(pageable);
        }
        model.addAttribute("posts", page.getContent());
        model.addAttribute("paging", page);
        model.addAttribute("search", search);

        return "posts";
    }
    @GetMapping("/{id}")
    public String getPostById(@PathVariable Long id, Model model) {
        Post post = postService.findPostByIdWithCommentsAndTags(id);
        model.addAttribute("post", post);
        return "post";
    }
    @PostMapping("/")
    public String addPost(
            @RequestParam("title") String title,
            @RequestParam(required = false) MultipartFile image,
            @RequestParam("tags") String tags,
            @RequestParam("text") String text,
            Model model) throws IOException {
        System.out.println("Title: " + title);
        System.out.println("Tags: " + tags);
        System.out.println("Text: " + text);
        byte[] imageBytes = (image != null) ? image.getBytes() : null;
        Post post = postService.createPost(title, imageBytes, tags, text);
        return "redirect:/posts/" + post.getId();
    }
    @PostMapping("/{id}")
    public String editPost(
            @PathVariable Long id,
            @RequestParam String title,
            @RequestParam(required = false) MultipartFile image,
            @RequestParam String tags,
            @RequestParam String text,
            Model model) throws IOException {

        byte[] imageBytes = (image != null) ? image.getBytes() : null;
        postService.updatePost(id, title, imageBytes, tags, text);
        return "redirect:/posts/" + id;
    }
    @GetMapping("/add")
    public String showCreatePostForm() {
        return "add-post";
    }

    @GetMapping("/{id}/edit")
    public String showEditPostForm(@PathVariable Long id, Model model) {
        model.addAttribute("post", postService.findById(id));
        return "add-post";
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
        postService.updatePost(id, title, imageBytes, tags, text);
        return "redirect:/posts/" + id;
    }

    @PostMapping("/{id}/delete")
    public String deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return "redirect:/posts";
    }


    @PostMapping("/{id}/like")
    public String likePost(@PathVariable Long id,
                           @RequestParam boolean like)
    {
        postService.likePost(id, like);
        return "redirect:/posts/" + id;
    }
}
