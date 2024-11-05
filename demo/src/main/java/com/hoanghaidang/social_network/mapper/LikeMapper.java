package com.hoanghaidang.social_network.mapper;

import com.hoanghaidang.social_network.dto.response.LikeCommentResponse;
import com.hoanghaidang.social_network.dto.response.LikePostResponse;
import com.hoanghaidang.social_network.dto.response.PostResponse;
import com.hoanghaidang.social_network.entity.LikeComment;
import com.hoanghaidang.social_network.entity.LikePost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LikeMapper {
    @Autowired
    private PostMapper postMapper;
    @Autowired
    private CommentMapper commentMapper;

    public LikePostResponse toPostResponse(LikePost like) {
        LikePostResponse likePostResponse = new LikePostResponse();
        likePostResponse.setPostResponse(postMapper.toPostResponse(like.getPost()));

        return likePostResponse;
    }

    public LikeCommentResponse toCommentResponse(LikeComment like) {
        LikeCommentResponse likeCommentResponse = new LikeCommentResponse();
        likeCommentResponse.setCommentResponse(commentMapper.commentResponse(like.getComment()));

        return likeCommentResponse;
    }
}
