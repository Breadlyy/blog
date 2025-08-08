package com.example.blog.controller;

import com.example.blog.model.Post;
import com.example.blog.service.CommentService;
import com.example.blog.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class MainController {

    private final PostService postService;
    private final CommentService commentService;


    @GetMapping("/posts")
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

    @GetMapping("/posts/{id}")
    public String getPostById(@PathVariable Long id, Model model) {
        Post post = postService.findPostByIdWithCommentsAndTags(id);
        model.addAttribute("post", post);
        return "post";
    }
    @PostMapping("/posts")
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
    @PostMapping("/posts/{id}")
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
    @GetMapping("/posts/add")
    public String showCreatePostForm() {
        return "add-post";
    }

    @GetMapping("/posts/{id}/edit")
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

    @PostMapping("/posts/{id}/delete")
    public String deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return "redirect:/posts";
    }

    @PostMapping("/posts/{postId}/comments")
    public String addComment(
            @PathVariable Long postId,
            @RequestParam String text) {
        commentService.addComment(postId, text);
        return "redirect:/posts/" + postId;
    }

    @PostMapping("/comments/{id}/delete")
    public String deleteComment(@PathVariable Long id, @RequestParam Long postId) {
            commentService.deleteComment(id);
            return "redirect:/posts/" + postId;
    }
    @GetMapping("/images/{postId}")
    public ResponseEntity<byte[]> getPostImage(@PathVariable Long postId) {
        Post post = postService.findById(postId);

        byte[] imageData = post.getImage();
        if (imageData == null || imageData.length == 0) {
            return ResponseEntity.notFound().build();
        }

        return new ResponseEntity<>(imageData, HttpStatus.OK);
    }
    @PostMapping("/posts/{id}/like")
    public String likePost(@PathVariable Long id,
                                           @RequestParam boolean like)
    {
        postService.likePost(id, like);
        return "redirect:/posts/" + id;
    }
    @PostMapping("/posts/{id}/comments/{commentId}")
    public String editComment(@PathVariable Long commentId,
                              @PathVariable("id") Long postId,
                              @RequestParam String text)
    {
        commentService.editComment(commentId, text);
        return "redirect:/posts/" + postId;
    }
    @PostMapping("/posts/{id}/comments/{commentId}/delete")
    public String deleteCommentById(@PathVariable Long commentId,
                                    @PathVariable("id") Long postId)
    {
        commentService.deleteComment(commentId);
        return "redirect:/posts/" + postId;
    }
}
