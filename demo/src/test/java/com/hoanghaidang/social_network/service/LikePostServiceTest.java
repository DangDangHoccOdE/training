package com.hoanghaidang.social_network.service;

import com.hoanghaidang.social_network.dao.*;
import com.hoanghaidang.social_network.dto.response.ApiResponse;
import com.hoanghaidang.social_network.dto.response.LikePostResponse;
import com.hoanghaidang.social_network.entity.*;
import com.hoanghaidang.social_network.enums.PostStatus;
import com.hoanghaidang.social_network.exception.CustomException;
import com.hoanghaidang.social_network.mapper.LikeMapper;
import com.hoanghaidang.social_network.service.impl.LikePostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LikePostServiceTest {
    @InjectMocks
    private LikePostService likePostService;
    @Mock
    private Authentication authentication;
    @Mock
    private LikePostRepository likePostRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private UserRepository userRepository;
    private User user;
    private Post post;
    @Mock
    private FriendShipRepository friendShipRepository;

    @Mock
    private LikeMapper likeMapper;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        user = User.builder().id(1L).email("a@gmail.com").build();
        post = Post.builder().id(1L).postStatus(PostStatus.PUBLIC).build();
    }

    private void mockAuthenticationAndUser(User user) {
        when(authentication.getName()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
    }

    @Test
    void testGetLikePostList_Success() {
        mockAuthenticationAndUser(user);
        Pageable pageable = PageRequest.of(0, 5, Sort.by("createAt").descending());

        LikePost likePost = LikePost.builder().id(1L).post(post).user(user).build();
        Page<LikePost> likePostPage = new PageImpl<>(List.of(likePost), pageable, 1);
        LikePostResponse likePostResponse = new LikePostResponse();

        when(likePostRepository.findLikePostByUser(user, pageable)).thenReturn(likePostPage);
        when(likeMapper.toPostResponse(any(LikePost.class))).thenReturn(likePostResponse);

        ResponseEntity<ApiResponse<Map<String, Object>>> response = likePostService.getLikePostList(authentication, 0, 5);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        Map<String, Object> responseData = response.getBody().getData();
        assertEquals(List.of(likePostResponse), responseData.get("likePosts"));
    }


    @Test
    void testLikePost_Success() {
        LikePost likePost = LikePost.builder().post(post).id(1L).build();
        ApiResponse<LikePostResponse> apiResponse = ApiResponse.<LikePostResponse>builder()
                .message("Like post is completed")
                .data(likeMapper.toPostResponse(likePost))
                .build();

        post.setUser(user);
        mockAuthenticationAndUser(user);
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));

        ResponseEntity<ApiResponse<LikePostResponse>> response = likePostService.likePost(authentication, post.getId());

        assertEquals(post.getLikeCount(), 1);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(apiResponse.getMessage(), Objects.requireNonNull(response.getBody()).getMessage());
        verify(likePostRepository, times(1)).save(any(LikePost.class));
    }

    @Test
    void testLikePost_FailAccessDenied() {
        post.setPostStatus(PostStatus.PRIVATE);
        post.setUser(User.builder().id(2L).build());
        mockAuthenticationAndUser(user);

        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> likePostService.likePost(authentication, post.getId()));

        assertEquals("You do not have access!", exception.getMessage());
    }

    @Test
    void testLikePost_FailStatusFriends_Only() {
        User other = User.builder().id(2L).build();
        post.setPostStatus(PostStatus.FRIENDS_ONLY);
        post.setUser(other);
        mockAuthenticationAndUser(user);

        when(friendShipRepository.findByUser1AndUser2(other, user)).thenThrow(new AccessDeniedException("You do not have access!"));
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> likePostService.likePost(authentication, post.getId()));

        assertEquals("You do not have access!", exception.getMessage());
    }


    @Test
    void testLikePost_FailNotFoundPost() {
        mockAuthenticationAndUser(user);
        when(postRepository.findById(post.getId())).thenThrow(new CustomException("The post is not found", HttpStatus.NOT_FOUND));

        CustomException exception = assertThrows(CustomException.class, () -> likePostService.likePost(authentication, post.getId()));

        assertEquals("The post is not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void testLikePost_FailLikeIsDuplicate() {
        LikePost like = LikePost.builder().user(user).post(post).build();

        post.setUser(user);
        mockAuthenticationAndUser(user);
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        when(likePostRepository.findByUserIdAndPostId(user.getId(), post.getId())).thenReturn(like);

        CustomException exception = assertThrows(CustomException.class, () -> likePostService.likePost(authentication, post.getId()));

        assertEquals("Like is duplicate!", exception.getMessage());
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
    }

    @Test
    void unlikePost_Success() {
        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .message("Delete like is completed")
                .build();
        post.setLikeCount(1);
        LikePost like = LikePost.builder().id(1L).user(user).post(post).build();
        mockAuthenticationAndUser(user);
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        when(likePostRepository.findByUserIdAndPostId(any(), any())).thenReturn(like);

        ResponseEntity<ApiResponse<Void>> response = likePostService.unlike(authentication, like.getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(apiResponse.getMessage(), Objects.requireNonNull(response.getBody()).getMessage());
        assertEquals(post.getLikeCount(), 0);

        verify(likePostRepository).delete(like);
    }

    @Test
    void testUnlike_FailNotFoundLike() {
        mockAuthenticationAndUser(user);
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));

        CustomException exception = assertThrows(CustomException.class, () -> likePostService.unlike(authentication, post.getId()));

        assertEquals(exception.getMessage(), "Like post is not found!");
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }
}