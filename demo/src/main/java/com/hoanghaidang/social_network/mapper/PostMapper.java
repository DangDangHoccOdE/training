package com.hoanghaidang.social_network.mapper;

import com.hoanghaidang.social_network.dto.response.CommentResponse;
import com.hoanghaidang.social_network.dto.response.PostResponse;
import com.hoanghaidang.social_network.entity.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PostMapper {
    @Autowired
    private CommentMapper commentMapper;

    public PostResponse toPostResponse(Post post) {
        PostResponse.PostResponseBuilder builder = PostResponse.builder();

        if(post.getTitle() != null && !post.getTitle().isEmpty()) {
            builder.title(post.getTitle());
        }

        if(post.getContent() != null && !post.getContent().isEmpty()) {
            builder.content(post.getContent());
        }

        if(post.getImage() != null && !post.getImage().isEmpty()) {
            builder.images(post.getImage())
                    .imageCount(post.getImage().size());
        }else{
            builder.images(Collections.emptyList())
                    .imageCount(0);
        }

        if(post.getComments() != null && !post.getComments().isEmpty()) {
            List<CommentResponse> commentResponseList = post.getComments().stream()
                    .map(commentMapper::commentResponse)
                    .collect(Collectors.toList());
            builder.comments(commentResponseList);
        }

        builder.commentCount(post.getCommentCount())
                .createdAt(post.getCreateAt())
                .likeCount(post.getLikeCount())
                .postStatus(post.getPostStatus())
                .userId(post.getUser().getId())
                .id(post.getId());

        return builder.build();
    }
}
