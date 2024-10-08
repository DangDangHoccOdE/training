package com.luvina.training_final.SpringBootProject.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CommentDto {
    private long id;

    private List<String> image;

    @NotBlank(message = "Content is required")
    private String content;

    @NotNull(message = "User ID is required")
    private long userId;

    @NotNull
    private long postId;
}
