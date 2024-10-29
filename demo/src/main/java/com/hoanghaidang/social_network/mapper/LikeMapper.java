package com.hoanghaidang.social_network.mapper;

import com.hoanghaidang.social_network.dto.response.LikeCommentResponse;
import com.hoanghaidang.social_network.dto.response.LikePostResponse;
import com.hoanghaidang.social_network.entity.LikeComment;
import com.hoanghaidang.social_network.entity.LikePost;
import org.springframework.stereotype.Component;

@Component
public class LikeMapper {
    public LikePostResponse toPostResponse(LikePost like) {
        LikePostResponse likePostResponse = new LikePostResponse();
        likePostResponse.setPostId(like.getId());

        return likePostResponse;
    }

    public LikeCommentResponse toCommentResponse(LikeComment like) {
        LikeCommentResponse likeCommentResponse = new LikeCommentResponse();
        likeCommentResponse.setCommentId(like.getId());

        return likeCommentResponse;
    }
}
