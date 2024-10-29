package com.hoanghaidang.social_network.service.impl;

import com.hoanghaidang.social_network.dao.PostRepository;
import com.hoanghaidang.social_network.dao.UserRepository;
import com.hoanghaidang.social_network.dto.request.PostDto;
import com.hoanghaidang.social_network.dto.response.ApiResponse;
import com.hoanghaidang.social_network.dto.response.PostResponse;
import com.hoanghaidang.social_network.entity.Notice;
import com.hoanghaidang.social_network.entity.Post;
import com.hoanghaidang.social_network.entity.User;
import com.hoanghaidang.social_network.exception.CustomException;
import com.hoanghaidang.social_network.mapper.PostMapper;
import com.hoanghaidang.social_network.service.inter.IPostService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostService implements IPostService {
    PostRepository postRepository;
    UserRepository userRepository;
    PostMapper postMapper;

    private User getAuthenticatedUser(Authentication authentication) {
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new CustomException("User is not found", HttpStatus.NOT_FOUND));
    }

    private void checkPostOwnership(User user, Post post) {
        if (post.getUser() != user) {
            throw new AccessDeniedException("You do not have access");
        }
    }

    private Post getPost(long postId){
        return postRepository.findById(postId)
                .orElseThrow(() -> new CustomException("Post could not be found", HttpStatus.NOT_FOUND));
    }
    @Override
    public ResponseEntity<ApiResponse<PostResponse>> createPost(Authentication authentication, PostDto postDto) {
        if (postDto.getContent() == null && postDto.getTitle() == null && postDto.getImage() == null) {
            throw new CustomException("Post is required a content or a title or images", HttpStatus.BAD_REQUEST);
        }

        User user = getAuthenticatedUser(authentication);

        Post post = Post.builder()
                .title(postDto.getTitle())
                .content(postDto.getContent())
                .createAt(LocalDateTime.now())
                .status(postDto.getStatus())
                .image(postDto.getImage())
                .user(user)
                .build();

        postRepository.save(post);

        PostResponse postResponse = postMapper.toPostResponse(post);

        ApiResponse<PostResponse> apiResponse = ApiResponse.<PostResponse>builder()
                .message("Create post completed!")
                .data(postResponse)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @Override
    public ResponseEntity<ApiResponse<PostResponse>> editPost(Authentication authentication, long postId, PostDto postDto) {
        if (postDto.getContent() == null && postDto.getTitle() == null && postDto.getImage() == null) {
            throw new CustomException("Post is required a content or a title or images", HttpStatus.BAD_REQUEST);
        }
        Post post = getPost(postId);

        User user = getAuthenticatedUser(authentication);
        checkPostOwnership(user, post);

        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());
        post.setStatus(postDto.getStatus());
        post.setUpdateAt(LocalDateTime.now());
        post.setImage(postDto.getImage());

        postRepository.save(post);

        PostResponse postResponse = postMapper.toPostResponse(post);

        ApiResponse<PostResponse> apiResponse = ApiResponse.<PostResponse>builder()
                .message("Edit post completed")
                .data(postResponse)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> deletePost(Authentication authentication, long postId) {
        Post post = getPost(postId);

        User user = getAuthenticatedUser(authentication);
        checkPostOwnership(user, post);

        postRepository.delete(post);

        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .message("Delete post completed")
                .build();
        return ResponseEntity.ok(apiResponse);
    }
}

