package com.hoanghaidang.social_network.dto.request;

import jakarta.validation.constraints.NotNull;
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

    private String content;
}
