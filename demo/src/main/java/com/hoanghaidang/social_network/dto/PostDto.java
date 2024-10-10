package com.hoanghaidang.social_network.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PostDto {
    @NotBlank(message = "Title is required")
    @Size(max = 255,message = "Title must not exceed 255 characters")
    private String title;

    @NotBlank(message = "Content is required")
    private String content;

    @NotBlank(message = "Status is required")
    @Pattern(regexp = "Public|Private",message = "Status must be 'Public' or 'Private'")
    private String status;

    private List<String> image;

}