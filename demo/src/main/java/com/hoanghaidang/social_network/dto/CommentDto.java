package com.hoanghaidang.social_network.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class CommentDto {
    @NotNull(message = "PostId is required")
    private long postId;

    private List<String> image;

    private String content;
}
