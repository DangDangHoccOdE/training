package com.hoanghaidang.social_network.service.impl;

import com.hoanghaidang.social_network.dao.CommentRepository;
import com.hoanghaidang.social_network.dao.LikePostRepository;
import com.hoanghaidang.social_network.dao.PostRepository;
import com.hoanghaidang.social_network.dao.UserRepository;
import com.hoanghaidang.social_network.dto.response.ApiResponse;
import com.hoanghaidang.social_network.dto.response.LikePostResponse;
import com.hoanghaidang.social_network.entity.*;
import com.hoanghaidang.social_network.exception.CustomException;
import com.hoanghaidang.social_network.mapper.LikeMapper;
import com.hoanghaidang.social_network.service.inter.ILikeCommentService;
import com.hoanghaidang.social_network.service.inter.ILikePostService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LikePostService implements ILikePostService {
    LikePostRepository likePostRepository;
    PostRepository postRepository;
    UserRepository userRepository;
    LikeMapper likeMapper;

    private User getAuthenticatedUser(Authentication authentication) {
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new CustomException("User is not found", HttpStatus.NOT_FOUND));
    }

    private LikePost checkDuplicateLike(long userId, Long postId) {
            return likePostRepository.findByUserIdAndPostId(userId, postId);
    }

    @Override
    public ResponseEntity<ApiResponse<LikePostResponse>> likePost(Authentication authentication, long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException("The post is not found", HttpStatus.NOT_FOUND));

        User user = getAuthenticatedUser(authentication);

        if (checkDuplicateLike(user.getId(), postId) != null) {
            throw new CustomException("Like is duplicate!", HttpStatus.CONFLICT);
        }

        LikePost likePost = LikePost.builder()
                .createAt(LocalDateTime.now())
                .post(post)
                .user(user)
                .build();

        post.setLikeCount(post.getLikeCount() + 1);
        likePostRepository.save(likePost);
        LikePostResponse likePostResponse = likeMapper.toPostResponse(likePost);

        ApiResponse<LikePostResponse> apiResponse = ApiResponse.<LikePostResponse>builder()
                .message("Like post is completed")
                .data(likePostResponse)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> unlike(Authentication authentication, long postId) {
        User user = getAuthenticatedUser(authentication);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException("The post is not found", HttpStatus.NOT_FOUND));

        LikePost likePost = likePostRepository.findByUserIdAndPostId(user.getId(), postId);

        if(likePost == null){
            throw new CustomException("Like post is not found!",HttpStatus.NOT_FOUND);
        }

        post.setLikeCount(post.getLikeCount() - 1);
        postRepository.save(post);

        likePostRepository.delete(likePost);
        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .message("Delete like is completed")
                .build();
        return ResponseEntity.ok(apiResponse);
    }
}

