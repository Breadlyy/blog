package com.example.blog.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class PostRequest {

    @NotBlank(message = "Title cannot be empty")
    @Size(max = 255, message = "Title cannot be longer than 255 characters")
    private String title;

    private MultipartFile image;

    @NotBlank(message = "Tags cannot be empty")
    private String tags;

    @NotBlank(message = "Text cannot be empty")
    private String text;

}
