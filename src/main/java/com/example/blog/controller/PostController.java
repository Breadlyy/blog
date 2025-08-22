package com.example.blog.controller;

import com.example.blog.model.Post;
import com.example.blog.model.dto.PostRequest;
import com.example.blog.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@Controller
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping
    public String getAllPosts(
            @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "search", required = false) String search,
            Model model
    ) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Post> page = (search != null && !search.isEmpty())
                ? postService.findByTag(search, pageable)
                : postService.findAll(pageable);

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

    @GetMapping("/add")
    public String showCreatePostForm(Model model) {
        model.addAttribute("postRequest", new PostRequest());
        return "add-post";
    }

    @PostMapping
    public String addPost(@Valid @ModelAttribute("postRequest") PostRequest postRequest,
                          BindingResult bindingResult,
                          Model model) throws IOException {
        if (bindingResult.hasErrors()) {
            return "add-post";
        }

        byte[] imageBytes = (postRequest.getImage() != null) ? postRequest.getImage().getBytes() : null;
        Post post = postService.createPost(
                postRequest.getTitle(),
                imageBytes,
                postRequest.getTags(),
                postRequest.getText()
        );

        return "redirect:/posts/" + post.getId();
    }

    @GetMapping("/{id}/edit")
    public String showEditPostForm(@PathVariable Long id, Model model) {
        Post post = postService.findById(id);
        model.addAttribute("post", post);
        return "add-post";
    }

    @PostMapping("/{id}")
    public String editPost(@PathVariable Long id,
                           @Valid @ModelAttribute("postRequest") PostRequest postRequest,
                           BindingResult bindingResult,
                           Model model) throws IOException {
        if (bindingResult.hasErrors()) {
            return "add-post";
        }

        byte[] imageBytes = (postRequest.getImage() != null) ? postRequest.getImage().getBytes() : null;
        postService.updatePost(id,
                postRequest.getTitle(),
                imageBytes,
                postRequest.getTags(),
                postRequest.getText()
        );

        return "redirect:/posts/" + id;
    }

    @PostMapping("/{id}/delete")
    public String deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return "redirect:/posts";
    }

    @PostMapping("/{id}/like")
    public String likePost(@PathVariable Long id, @RequestParam boolean like) {
        postService.likePost(id, like);
        return "redirect:/posts/" + id;
    }
}
