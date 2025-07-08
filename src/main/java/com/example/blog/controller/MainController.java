package com.example.blog.controller;

import com.example.blog.model.Comment;
import com.example.blog.model.Post;
import com.example.blog.model.Tag;
import com.example.blog.service.CommentService;
import com.example.blog.service.PostService;
import com.example.blog.service.TagService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

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
        System.out.println(page.toString());
        model.addAttribute("posts", page.getContent());
        model.addAttribute("paging", page);
        model.addAttribute("search", search);

        return "posts";
    }

    @GetMapping("/posts/{id}")
    public String getPostById(@PathVariable Long id, Model model) {
        Post post = postService.findByIdWithCommentsAndTags(id);
        model.addAttribute("post", post);
        return "post";
    }

    @GetMapping("/posts/add")
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
    @GetMapping("/images/{postId}")
    public ResponseEntity<byte[]> getPostImage(@PathVariable Long postId) {
        Post post = postService.findById(postId);

        byte[] imageData = post.getImage();
        if (imageData == null || imageData.length == 0) {
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        headers.setContentLength(imageData.length);

        return new ResponseEntity<>(imageData, headers, HttpStatus.OK);
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
        commentService.editComment(postId, commentId, text);
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
