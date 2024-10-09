package com.hoanghaidang.social_network.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CommentDto {
    private List<String> image;

    @NotBlank(message = "Content is required")
    private String content;
}
