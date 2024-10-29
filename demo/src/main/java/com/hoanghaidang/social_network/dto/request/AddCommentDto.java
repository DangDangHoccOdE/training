package com.hoanghaidang.social_network.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class AddCommentDto {
    @NotNull(message = "PostId is required")
    private long postId;

    private List<String> image;

    @Size(max = 255,message = "Content must not exceed 255 characters")
    private String content;
}
