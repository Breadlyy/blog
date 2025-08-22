package com.example.blog.controller;

import com.example.blog.model.Post;
import com.example.blog.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class MainController {

    private final PostService postService;

    @GetMapping("/images/{postId}")
    public ResponseEntity<byte[]> getPostImage(@PathVariable Long postId) {
        Post post = postService.findById(postId);

        byte[] imageData = post.getImage();
        if (imageData == null || imageData.length == 0) {
            return ResponseEntity.notFound().build();
        }

        return new ResponseEntity<>(imageData, HttpStatus.OK);
    }


}
