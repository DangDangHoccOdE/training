package com.hoanghaidang.social_network.service;

import com.hoanghaidang.social_network.dao.CommentRepository;
import com.hoanghaidang.social_network.dao.PostRepository;
import com.hoanghaidang.social_network.dao.UserRepository;
import com.hoanghaidang.social_network.dto.request.AddCommentDto;
import com.hoanghaidang.social_network.dto.request.EditCommentDto;
import com.hoanghaidang.social_network.dto.response.ApiResponse;
import com.hoanghaidang.social_network.dto.response.CommentResponse;
import com.hoanghaidang.social_network.entity.Comment;
import com.hoanghaidang.social_network.entity.Post;
import com.hoanghaidang.social_network.entity.User;
import com.hoanghaidang.social_network.exception.CustomException;
import com.hoanghaidang.social_network.mapper.CommentMapper;
import com.hoanghaidang.social_network.service.impl.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CommentServiceTest {
    @InjectMocks
    private CommentService commentService;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private Authentication authentication;

    private CommentResponse commentResponse;
    private User mockUser;
    private Post mockPost;
    private Comment mockComment;
    private AddCommentDto addCommentDto;
    private EditCommentDto editCommentDto;

    @Mock
    private CommentMapper commentMapper;

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);

        mockUser = new User();
        mockUser.setEmail("a@gmail.com");

        mockPost = new Post();
        mockPost.setId(1L);
        mockPost.setCommentCount(0);

        mockComment = new Comment();
        mockComment.setId(1L);
        mockComment.setPost(mockPost);
        mockComment.setUser(mockUser);
        mockComment.setCreateAt(LocalDateTime.now());

        addCommentDto = AddCommentDto.builder()
                .content("a")
                .postId(1L)
                .build();

        editCommentDto = EditCommentDto.builder()
                .content("a")
                .build();

        commentResponse = new CommentResponse();
    }

    @Test
    void testCreateComment_Success(){
        ApiResponse<CommentResponse> apiResponse = ApiResponse.<CommentResponse>builder()
                .message("Create comment completed")
                .data(commentResponse)
                .build();

        mockAuthenticationAndUser(mockUser);
        when(postRepository.findById(mockPost.getId())).thenReturn(Optional.of(mockPost));

        ResponseEntity<ApiResponse<CommentResponse>> response = commentService.createComment(authentication,mockPost.getId(), addCommentDto);

        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertEquals(apiResponse.getMessage(), Objects.requireNonNull(response.getBody()).getMessage());
        assertEquals(1,mockPost.getCommentCount());
        verify(commentRepository).save(any(Comment.class));
        verify(postRepository).save(mockPost);
    }

    @Test
    void testCreateComment_FailNotFoundUser(){
        when(postRepository.findById(mockPost.getId())).thenReturn(Optional.of(mockPost));
        when(userRepository.findById(mockUser.getId())).thenThrow(new CustomException("User is not found",HttpStatus.NOT_FOUND));

        CustomException exception = assertThrows(CustomException.class, () -> commentService.createComment(authentication,mockPost.getId(), addCommentDto));

        assertEquals(exception.getMessage(), "User is not found");
        assertEquals(exception.getStatus(), HttpStatus.NOT_FOUND);
    }

    @Test
    void testCreateComment_FailNotFoundPost(){
        mockAuthenticationAndUser(mockUser);
        when(postRepository.findById(mockPost.getId())).thenThrow(new CustomException("Post is not found",HttpStatus.NOT_FOUND));

        CustomException exception = assertThrows(CustomException.class, () -> commentService.createComment(authentication,mockPost.getId(), addCommentDto));

        assertEquals(exception.getMessage(), "Post is not found");
        assertEquals(exception.getStatus(), HttpStatus.NOT_FOUND);
    }

    @Test
    void testCreateComment_FailEmptyContentAndImage(){
        addCommentDto.setContent(null);
        addCommentDto.setImage(null);

        mockAuthenticationAndUser(mockUser);
        when(postRepository.findById(mockPost.getId())).thenReturn(Optional.of(mockPost));

        CustomException exception = assertThrows(CustomException.class,()-> commentService.createComment(authentication,mockPost.getId(), addCommentDto));

        assertEquals("Images or Content is required",exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void testEditComment_FailNotOwner(){
        User other = new User();
        other.setEmail("b@gmail.com");
        other.setId(2L);

        mockAuthenticationAndUser(other);
        when(commentRepository.findById(mockComment.getId())).thenReturn(Optional.of(mockComment));

        AccessDeniedException exception = assertThrows(AccessDeniedException.class,()-> commentService.editComment(authentication,mockComment.getId(), editCommentDto));

        assertEquals("You do not have access!",exception.getMessage());
    }

    @Test
    void testEditComment_Success(){
        ApiResponse<CommentResponse> apiResponse = ApiResponse.<CommentResponse>builder()
                .message("Edit comment completed")
                .data(commentResponse)
                .build();

        mockAuthenticationAndUser(mockUser);
        when(commentRepository.findById(mockComment.getId())).thenReturn(Optional.of(mockComment));

        commentResponse.setContent(editCommentDto.getContent());
        when(commentMapper.commentResponse(any())).thenReturn(commentResponse);
        ResponseEntity<ApiResponse<CommentResponse>> response = commentService.editComment(authentication,mockComment.getId(), editCommentDto);

        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertEquals(apiResponse.getMessage(), Objects.requireNonNull(response.getBody()).getMessage());
        verify(commentRepository).save(mockComment);
    }

    @Test
    void testEditComment_FailNotFoundComment(){
        when(commentRepository.findById(mockComment.getId())).thenThrow(new CustomException("Comment is not found",HttpStatus.NOT_FOUND));
        mockAuthenticationAndUser(mockUser);

        CustomException exception = assertThrows(CustomException.class,() -> commentService.editComment(authentication,mockComment.getId(), editCommentDto));
        assertEquals(exception.getMessage(),"Comment is not found");
        assertEquals(exception.getStatus(),HttpStatus.NOT_FOUND);
    }

    @Test
    void testEditComment_FailValidInfoEmpty(){
        editCommentDto.setContent(null);
        editCommentDto.setImage(null);

        when(commentRepository.findById(mockComment.getId())).thenReturn(Optional.of(mockComment));
        mockAuthenticationAndUser(mockUser);

        CustomException exception = assertThrows(CustomException.class,() -> commentService.editComment(authentication,mockComment.getId(), editCommentDto));
        assertEquals(exception.getMessage(),"Images or Content is required");
        assertEquals(exception.getStatus(),HttpStatus.BAD_REQUEST);
    }

    @Test
    void deleteComment_Success(){
        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .message("Delete comment completed")
                .build();

        mockAuthenticationAndUser(mockUser);
        when(commentRepository.findById(mockComment.getId())).thenReturn(Optional.of(mockComment));

        ResponseEntity<ApiResponse<Void>> response = commentService.deleteComment(authentication,mockComment.getId());

        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertEquals(apiResponse.getMessage(),(Objects.requireNonNull(response.getBody())).getMessage());
        assertEquals(-1,mockPost.getCommentCount());
        verify(commentRepository).delete(mockComment);
        verify(postRepository).save(mockPost);
    }

    @Test
    void testDeleteComment_FailNotOwner(){
        User other = new User();
        other.setId(2L);
        other.setEmail("b@gmail.com");

        mockAuthenticationAndUser(other);
        when(commentRepository.findById(mockComment.getId())).thenReturn(Optional.of(mockComment));

        AccessDeniedException exception = assertThrows(AccessDeniedException.class,()-> commentService.deleteComment(authentication,mockComment.getId()));

        assertEquals("You do not have access!",exception.getMessage());
    }

    private void mockAuthenticationAndUser(User user) {
        when(authentication.getName()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
    }
}
