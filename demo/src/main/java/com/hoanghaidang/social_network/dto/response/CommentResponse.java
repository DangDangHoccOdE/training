package com.hoanghaidang.social_network.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentResponse {
    private long id;
    private long imageCount;
    private String content;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private long userId;
    private List<String> images;
    private long likeCount;
    private long postId;
}
