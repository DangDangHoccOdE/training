package com.hoanghaidang.social_network.service.impl;

import com.hoanghaidang.social_network.dao.*;
import com.hoanghaidang.social_network.dto.response.ApiResponse;
import com.hoanghaidang.social_network.dto.response.LikePostResponse;
import com.hoanghaidang.social_network.entity.*;
import com.hoanghaidang.social_network.enums.FriendStatus;
import com.hoanghaidang.social_network.enums.PostStatus;
import com.hoanghaidang.social_network.exception.CustomException;
import com.hoanghaidang.social_network.mapper.LikeMapper;
import com.hoanghaidang.social_network.service.inter.ILikePostService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LikePostService implements ILikePostService {
    LikePostRepository likePostRepository;
    PostRepository postRepository;
    UserRepository userRepository;
    LikeMapper likeMapper;
    FriendShipRepository friendShipRepository;

    private User getAuthenticatedUser(Authentication authentication) {
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new CustomException("User is not found", HttpStatus.NOT_FOUND));
    }

    private LikePost checkDuplicateLike(long userId, Long postId) {
            return likePostRepository.findByUserIdAndPostId(userId, postId);
    }

    private FriendShip findFriendship(User user1, User user2) {
        return friendShipRepository.findByUser1AndUser2(user1, user2)
                .or(() -> friendShipRepository.findByUser1AndUser2(user2, user1))
                .orElseThrow(() -> new AccessDeniedException("You do not have access!"));
    }

    @Override
    public ResponseEntity<ApiResponse<Map<String, Object>>> getLikePostList(Authentication authentication, int page, int size) {
        User user = getAuthenticatedUser(authentication);

        Pageable pageable = PageRequest.of(page, size, Sort.by("createAt").descending());

        Page<LikePost> likePosts = likePostRepository.findLikePostByUser(user, pageable);

        Page<LikePostResponse> likePostResponses = likePosts.map(
                likeMapper::toPostResponse
        );

        Map<String, Object> response = new HashMap<>();
        response.put("likePosts", likePostResponses.getContent());
        response.put("currentPage", likePostResponses.getNumber());
        response.put("totalItems", likePostResponses.getTotalElements());
        response.put("totalPages", likePostResponses.getTotalPages());

        ApiResponse<Map<String,Object>> apiResponse = ApiResponse.<Map<String,Object>>builder()
                .data(response)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @Override
    public ResponseEntity<ApiResponse<LikePostResponse>> likePost(Authentication authentication, long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException("The post is not found", HttpStatus.NOT_FOUND));

        User user = getAuthenticatedUser(authentication);

        User auth = post.getUser();
        if(user.getId()!=auth.getId() && post.getPostStatus().equals(PostStatus.PRIVATE)){
            throw new AccessDeniedException("You do not have access!");
        }

        if(post.getPostStatus().equals(PostStatus.FRIENDS_ONLY) ){
            FriendShip friendShip = findFriendship(user, auth);
            if(friendShip != null && !friendShip.getStatus().equals(FriendStatus.ACCEPTED)){
                throw new AccessDeniedException("You do not have access!");
            }
        }

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

