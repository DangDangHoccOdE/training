package com.hoanghaidang.social_network.service.impl;

import com.hoanghaidang.social_network.dao.FriendShipRepository;
import com.hoanghaidang.social_network.dao.PostRepository;
import com.hoanghaidang.social_network.dao.UserRepository;
import com.hoanghaidang.social_network.dto.request.PostDto;
import com.hoanghaidang.social_network.dto.response.ApiResponse;
import com.hoanghaidang.social_network.dto.response.PostResponse;
import com.hoanghaidang.social_network.entity.FriendShip;
import com.hoanghaidang.social_network.entity.Post;
import com.hoanghaidang.social_network.entity.User;
import com.hoanghaidang.social_network.enums.FriendStatus;
import com.hoanghaidang.social_network.enums.PostStatus;
import com.hoanghaidang.social_network.exception.CustomException;
import com.hoanghaidang.social_network.mapper.PostMapper;
import com.hoanghaidang.social_network.service.inter.IPostService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
public class PostService implements IPostService {
    PostRepository postRepository;
    UserRepository userRepository;
    PostMapper postMapper;
    ImageService imageService;
    FriendShipRepository friendShipRepository;

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

    private FriendShip findFriendship(User user1, User user2) {
        return friendShipRepository.findByUser1AndUser2(user1, user2)
                .or(() -> friendShipRepository.findByUser1AndUser2(user2, user1))
                .orElseThrow(() -> new AccessDeniedException("You do not have access!"));
    }

    @Override
    public ResponseEntity<ApiResponse<PostResponse>> getPostById(Authentication authentication, long postId) {
        User user = getAuthenticatedUser(authentication);

        Post post = getPost(postId);
        if(post.getUser().getId()!=user.getId() && !post.getPostStatus().equals(PostStatus.PUBLIC)){
            User ownerPost =  post.getUser();

            FriendShip friendShip = findFriendship(user, ownerPost);

            if((post.getPostStatus().equals(PostStatus.FRIENDS_ONLY) && !friendShip.getStatus().equals(FriendStatus.ACCEPTED))
                    || post.getPostStatus().equals(PostStatus.PRIVATE) ){
                throw new AccessDeniedException("You do not have access!");
            }
        }

        ApiResponse<PostResponse> response = ApiResponse.<PostResponse>builder()
                .data(postMapper.toPostResponse(post))
                .build();
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ApiResponse<Map<String,Object>>> timeline(Authentication authentication, int page, int size) {
        User user = getAuthenticatedUser(authentication);

        Pageable pageable = PageRequest.of(page, size);

        Page<Post> posts = postRepository.findFriendPostsByEmail(user.getEmail(), pageable);
        Page<PostResponse> postResponses = posts.map(postMapper::toPostResponse);

        Map<String, Object> response = new HashMap<>();
        response.put("posts", postResponses.getContent());
        response.put("currentPage", postResponses.getNumber());
        response.put("totalItems", postResponses.getTotalElements());
        response.put("totalPages", postResponses.getTotalPages());

        ApiResponse<Map<String,Object>> apiResponse = ApiResponse.<Map<String,Object>>builder()
                .data(response)
                .build();
        return ResponseEntity.ok(apiResponse);
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
                .postStatus(postDto.getPostStatus())
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
        post.setPostStatus(postDto.getPostStatus());
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

        if(post.getImage()!=null){
            for(String path : post.getImage()){
                String sanitizedPath = path.replace("/", "");
                imageService.deleteImageFile(sanitizedPath);
            }
        }

        postRepository.delete(post);

        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .message("Delete post completed")
                .build();
        return ResponseEntity.ok(apiResponse);
    }
}

