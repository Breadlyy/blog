package com.example.blog.controller;

import com.example.blog.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;
    @PostMapping("/comments/{id}/delete")
    public String deleteComment(@PathVariable Long id, @RequestParam Long postId) {
        commentService.deleteComment(id);
        return "redirect:/posts/" + postId;
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
    @PostMapping("/{postId}/comments")
    public String addComment(
            @PathVariable Long postId,
            @RequestParam String text) {
        commentService.addComment(postId, text);
        return "redirect:/posts/" + postId;
    }
}
