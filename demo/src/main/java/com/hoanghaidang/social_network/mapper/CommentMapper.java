package com.hoanghaidang.social_network.mapper;

import com.hoanghaidang.social_network.dto.response.CommentResponse;
import com.hoanghaidang.social_network.entity.Comment;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class CommentMapper {
    public CommentResponse commentResponse(Comment comment) {
        CommentResponse.CommentResponseBuilder responseBuilder = CommentResponse.builder();

        // Kiểm tra và set content nếu có
        if (comment.getContent() != null && !comment.getContent().isEmpty()) {
            responseBuilder.content(comment.getContent());
        }

        // Kiểm tra và set images nếu có
        if (comment.getImage() != null && !comment.getImage().isEmpty()) {
            responseBuilder.images(comment.getImage())
                    .imageCount(comment.getImage().size());
        } else {
            // Nếu không có ảnh, set images là một danh sách trống và imageCount là 0
            responseBuilder.images(Collections.emptyList())
                    .imageCount(0);
        }

        // Set các trường khác
        responseBuilder.createAt(comment.getCreateAt())
                .updateAt(comment.getUpdateAt())
                .userId(comment.getUser().getId())
                .postId(comment.getPost().getId())
                .likeCount(comment.getLikeCount());

        return responseBuilder.build();
    }
}
