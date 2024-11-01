package com.hoanghaidang.social_network.service;

import com.hoanghaidang.social_network.dao.CommentRepository;
import com.hoanghaidang.social_network.dao.LikeCommentRepository;
import com.hoanghaidang.social_network.dao.UserRepository;
import com.hoanghaidang.social_network.dto.response.ApiResponse;
import com.hoanghaidang.social_network.dto.response.LikeCommentResponse;
import com.hoanghaidang.social_network.entity.*;
import com.hoanghaidang.social_network.exception.CustomException;
import com.hoanghaidang.social_network.mapper.LikeMapper;
import com.hoanghaidang.social_network.service.impl.LikeCommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class LikeCommentServiceTest {
    @InjectMocks
    private LikeCommentService likeCommentService;
    @Mock
    private Authentication authentication;
    @Mock
    private LikeCommentRepository likeCommentRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private UserRepository userRepository;
    private User user;
    private Post post;
    private Comment comment;

    @Mock
    private LikeMapper likeMapper;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        user = User.builder().id(1L).email("a@gmail.com").build();
        comment = Comment.builder().id(1L).build();
    }

    private void mockAuthenticationAndUser(User user) {
        when(authentication.getName()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
    }

    @Test
    void testLikeComment_Success() {
        LikeComment likeComment = LikeComment.builder().id(1L).build();
        ApiResponse<LikeCommentResponse> apiResponse = ApiResponse.<LikeCommentResponse>builder()
                .message("Like comment is completed")
                .data(likeMapper.toCommentResponse(likeComment))
                .build();

        mockAuthenticationAndUser(user);
        when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));

        ResponseEntity<ApiResponse<LikeCommentResponse>> response = likeCommentService.likeComment(authentication, comment.getId());

        assertEquals(comment.getLikeCount(), 1);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(apiResponse.getMessage(), Objects.requireNonNull(response.getBody()).getMessage());
        verify(likeCommentRepository, times(1)).save(any(LikeComment.class));
    }

    @Test
    void testLikeComment_FailNotFoundComment() {
        mockAuthenticationAndUser(user);
        when(commentRepository.findById(comment.getId())).thenThrow(new CustomException("The comment is not found", HttpStatus.NOT_FOUND));

        CustomException exception = assertThrows(CustomException.class, () -> likeCommentService.likeComment(authentication, comment.getId()));

        assertEquals("The comment is not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void testLikeComment_FailLikeIsDuplicate() {
        LikeComment like = LikeComment.builder().user(user).comment(comment).build();

        mockAuthenticationAndUser(user);
        when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));
        when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));
        when(likeCommentRepository.findByUserIdAndCommentId(user.getId(), comment.getId())).thenReturn(like);

        CustomException exception = assertThrows(CustomException.class, () -> likeCommentService.likeComment(authentication, comment.getId()));

        assertEquals("Like is duplicate!", exception.getMessage());
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
    }

    @Test
    void unlikeComment_Success() {
        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .message("Delete like is completed")
                .build();
        comment.setLikeCount(1);
        LikeComment like = LikeComment.builder().id(1L).user(user).comment(comment).build();
        mockAuthenticationAndUser(user);
        when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));
        when(likeCommentRepository.findByUserIdAndCommentId(any(), any())).thenReturn(like);

        ResponseEntity<ApiResponse<Void>> response = likeCommentService.unlike(authentication, like.getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(apiResponse.getMessage(), Objects.requireNonNull(response.getBody()).getMessage());
        assertEquals(comment.getLikeCount(), 0);

        verify(likeCommentRepository).delete(like);
    }

    @Test
    void testUnlike_FailNotFoundLike() {
        mockAuthenticationAndUser(user);
        when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));

        CustomException exception = assertThrows(CustomException.class, () -> likeCommentService.unlike(authentication, comment.getId()));

        assertEquals(exception.getMessage(), "Like comment is not found!");
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }
}