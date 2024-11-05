package com.hoanghaidang.social_network.dto.response;

import com.hoanghaidang.social_network.enums.PostStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostResponse {
    private long id;
    private long imageCount;
    private long likeCount;
    private long commentCount;
    private long userId;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private PostStatus postStatus;
    private List<String> images;
    private List<CommentResponse> commentResponses ;
}
