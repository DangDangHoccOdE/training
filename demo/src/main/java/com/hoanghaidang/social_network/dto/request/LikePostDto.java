package com.hoanghaidang.social_network.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LikePostDto {
    @NotNull(message = "PostId is required")
    private long postId;
}
