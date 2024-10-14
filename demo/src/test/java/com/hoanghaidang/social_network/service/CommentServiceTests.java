package com.hoanghaidang.social_network.service;

import com.hoanghaidang.social_network.dao.CommentRepository;
import com.hoanghaidang.social_network.dao.PostRepository;
import com.hoanghaidang.social_network.dao.UserRepository;
import com.hoanghaidang.social_network.dto.CommentDto;
import com.hoanghaidang.social_network.entity.Comment;
import com.hoanghaidang.social_network.entity.Notice;
import com.hoanghaidang.social_network.entity.Post;
import com.hoanghaidang.social_network.entity.User;
import com.hoanghaidang.social_network.exception.CustomException;
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
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CommentServiceTests {
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

    private User mockUser;
    private Post mockPost;
    private Comment mockComment;
    private CommentDto commentDto;

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

        commentDto = new CommentDto();
        commentDto.setContent("a");
        commentDto.setPostId(1L);
    }

    @Test
    void testCreateComment_Success(){
        mockAuthenticationAndUser(mockUser);
        when(postRepository.findById(mockPost.getId())).thenReturn(Optional.of(mockPost));

        ResponseEntity<?> response = commentService.createComment(authentication,mockPost.getId(),commentDto);

        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertEquals("Create comment completed",((Notice) response.getBody()).getMessage());
        assertEquals(1,mockPost.getCommentCount());
        verify(commentRepository).save(any(Comment.class));
        verify(postRepository).save(mockPost);
    }

    @Test
    void testCreateComment_FailNotFoundUser(){
        when(postRepository.findById(mockPost.getId())).thenReturn(Optional.of(mockPost));
        when(userRepository.findById(mockUser.getId())).thenThrow(new CustomException("User is not found",HttpStatus.NOT_FOUND));

        CustomException exception = assertThrows(CustomException.class, () -> commentService.createComment(authentication,mockPost.getId(),commentDto));

        assertEquals(exception.getMessage(), "User is not found");
        assertEquals(exception.getStatus(), HttpStatus.NOT_FOUND);
    }

    @Test
    void testCreateComment_FailNotFoundPost(){
        mockAuthenticationAndUser(mockUser);
        when(postRepository.findById(mockPost.getId())).thenThrow(new CustomException("Post is not found",HttpStatus.NOT_FOUND));

        CustomException exception = assertThrows(CustomException.class, () -> commentService.createComment(authentication,mockPost.getId(),commentDto));

        assertEquals(exception.getMessage(), "Post is not found");
        assertEquals(exception.getStatus(), HttpStatus.NOT_FOUND);
    }

    @Test
    void testCreateComment_FailEmptyContentAndImage(){
        commentDto.setContent(null);

        mockAuthenticationAndUser(mockUser);
        when(postRepository.findById(mockPost.getId())).thenReturn(Optional.of(mockPost));

        CustomException exception = assertThrows(CustomException.class,()-> commentService.createComment(authentication,mockPost.getId(),commentDto));

        assertEquals("Images or Content is required",exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void testEditComment_FailNotOwner(){
        User other = new User();
        other.setEmail("b@gmail.com");

        mockAuthenticationAndUser(other);
        when(commentRepository.findById(mockComment.getId())).thenReturn(Optional.of(mockComment));

        AccessDeniedException exception = assertThrows(AccessDeniedException.class,()-> commentService.editComment(authentication,mockComment.getId(),commentDto));

        assertEquals("You do not have access!",exception.getMessage());
    }

    @Test
    void testEditComment_Success(){
        mockAuthenticationAndUser(mockUser);
        when(commentRepository.findById(mockComment.getId())).thenReturn(Optional.of(mockComment));

        ResponseEntity<CommentDto> response = commentService.editComment(authentication,mockComment.getId(),commentDto);

        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertEquals(commentDto.getContent(), Objects.requireNonNull(response.getBody()).getContent());
        verify(commentRepository).save(mockComment);
    }

    @Test
    void testEditComment_FailNotFoundComment(){
        when(commentRepository.findById(mockComment.getId())).thenThrow(new CustomException("Comment is not found",HttpStatus.NOT_FOUND));
        mockAuthenticationAndUser(mockUser);

        CustomException exception = assertThrows(CustomException.class,() -> commentService.editComment(authentication,mockComment.getId(),commentDto));
        assertEquals(exception.getMessage(),"Comment is not found");
        assertEquals(exception.getStatus(),HttpStatus.NOT_FOUND);
    }

    @Test
    void deleteComment_Success(){
        mockAuthenticationAndUser(mockUser);
        when(commentRepository.findById(mockComment.getId())).thenReturn(Optional.of(mockComment));

        ResponseEntity<?> response = commentService.deleteComment(authentication,mockComment.getId());

        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertEquals("Delete comment completed",((Notice) response.getBody()).getMessage());
        assertEquals(-1,mockPost.getCommentCount());
        verify(commentRepository).delete(mockComment);
        verify(postRepository).save(mockPost);
    }

    private void mockAuthenticationAndUser(User user) {
        when(authentication.getName()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
    }
}
