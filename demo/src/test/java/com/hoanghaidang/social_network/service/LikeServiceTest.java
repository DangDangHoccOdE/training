package com.hoanghaidang.social_network.service;

import com.hoanghaidang.social_network.dao.CommentRepository;
import com.hoanghaidang.social_network.dao.LikeRepository;
import com.hoanghaidang.social_network.dao.PostRepository;
import com.hoanghaidang.social_network.dao.UserRepository;
import com.hoanghaidang.social_network.entity.*;
import com.hoanghaidang.social_network.exception.CustomException;
import com.hoanghaidang.social_network.service.impl.LikeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;

import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class LikeServiceTest {
    @InjectMocks
    private LikeService likeService;
    @Mock
    private Authentication authentication;
    @Mock
    private LikeRepository likeRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private UserRepository userRepository;
    private User user;
    private Post post;
    private Comment comment;
    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
        user = User.builder().id(1L).email("a@gmail.com").build();
        post = Post.builder().id(1L).status("Public").build();
        comment = Comment.builder().id(1L).build();
    }
    private void mockAuthenticationAndUser(User user){
        when(authentication.getName()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
    }

    @Test
    void testLikePost_Success() {
        mockAuthenticationAndUser(user);
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));

        ResponseEntity<Notice> response = likeService.likePost(authentication, post.getId());

        assertEquals(post.getLikeCount(),1);
        assertEquals(HttpStatus.OK,response.getStatusCode());
        verify(likeRepository,times(1)).save(any(Like.class));
    }

    @Test
    void testLikePost_FailStatusPrivate(){
        post.setStatus("Private");
        mockAuthenticationAndUser(user);
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));

        CustomException exception = assertThrows(CustomException.class,()->likeService.likePost(authentication,post.getId()));

        assertEquals("Post is private, cannot like!", exception.getMessage());
        assertEquals(HttpStatus.CONFLICT,exception.getStatus());
    }

    @Test
    void testLikePost_FailLikeIsDuplicate(){
        post.setStatus("Public");

        Like like = Like.builder().user(user).post(post).build();

        mockAuthenticationAndUser(user);
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        when(likeRepository.findByUserIdAndPostId(user.getId(),post.getId())).thenReturn(like);

        CustomException exception = assertThrows(CustomException.class,()->likeService.likePost(authentication,post.getId()));

        assertEquals("Like is duplicate!", exception.getMessage());
        assertEquals(HttpStatus.CONFLICT,exception.getStatus());
    }

    @Test
    void testLikePost_FailNotFoundPost(){
        mockAuthenticationAndUser(user);
        when(postRepository.findById(post.getId())).thenThrow(new CustomException("The post is not found",HttpStatus.NOT_FOUND));

        CustomException exception = assertThrows(CustomException.class,()->likeService.likePost(authentication,post.getId()));

        assertEquals("The post is not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND,exception.getStatus());
    }

    @Test
    void testLikeComment_Success() {
        mockAuthenticationAndUser(user);
        when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));

        ResponseEntity<Notice> response = likeService.likeComment(authentication, comment.getId());

        assertEquals(comment.getLikeCount(),1);
        assertEquals(HttpStatus.OK,response.getStatusCode());
        verify(likeRepository,times(1)).save(any(Like.class));
    }

    @Test
    void testLikeComment_FailNotFoundComment(){
        mockAuthenticationAndUser(user);
        when(commentRepository.findById(comment.getId())).thenThrow(new CustomException("The comment is not found",HttpStatus.NOT_FOUND));

        CustomException exception = assertThrows(CustomException.class,()->likeService.likeComment(authentication,comment.getId()));

        assertEquals("The comment is not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND,exception.getStatus());
    }

    @Test
    void testLikeComment_FailLikeIsDuplicate(){
        Like like = Like.builder().user(user).post(post).comment(comment).build();

        mockAuthenticationAndUser(user);
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));
        when(likeRepository.findByUserIdAndCommentId(user.getId(),comment.getId())).thenReturn(like);

        CustomException exception = assertThrows(CustomException.class,()->likeService.likeComment(authentication,comment.getId()));

        assertEquals("Like is duplicate!", exception.getMessage());
        assertEquals(HttpStatus.CONFLICT,exception.getStatus());
    }

    @Test
    void unlikePost_Success(){
        post.setLikeCount(1);
        Like like = Like.builder().id(1L).user(user).post(post).build();
        mockAuthenticationAndUser(user);
        when(likeRepository.findById(like.getId())).thenReturn(Optional.of(like));

        ResponseEntity<Notice> response = likeService.unlike(authentication,like.getId());

        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertEquals("Delete like is completed", Objects.requireNonNull(response.getBody()).getMessage());
        assertEquals(post.getLikeCount(),0);

        verify(likeRepository).delete(like);
    }

    @Test
    void unlikeComment_Success(){
        comment.setLikeCount(1);
        Like like = Like.builder().id(1L).user(user).comment(comment).build();
        mockAuthenticationAndUser(user);
        when(likeRepository.findById(like.getId())).thenReturn(Optional.of(like));

        ResponseEntity<Notice> response = likeService.unlike(authentication,like.getId());

        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertEquals("Delete like is completed", Objects.requireNonNull(response.getBody()).getMessage());
        assertEquals(comment.getLikeCount(),0);

        verify(likeRepository).delete(like);
    }

    @Test
    void testUnlike_FailNotFoundLike(){
        Like like = Like.builder().id(1L).build();
        mockAuthenticationAndUser(user);
        when(likeRepository.findById(like.getId())).thenThrow(new CustomException("Like is not found",HttpStatus.NOT_FOUND));

        CustomException exception = assertThrows(CustomException.class,()->likeService.unlike(authentication,like.getId()));

        assertEquals(exception.getMessage(),"Like is not found");
        assertEquals(HttpStatus.NOT_FOUND,exception.getStatus());
    }

    @Test
    void testUnlike_FailNotOwner(){
        User other = User.builder().id(2L).build();
        Like like = Like.builder().id(1L).user(other).build();
        mockAuthenticationAndUser(user);
        when(likeRepository.findById(like.getId())).thenReturn(Optional.of(like));

        AccessDeniedException exception = assertThrows(AccessDeniedException.class,()->likeService.unlike(authentication,like.getId()));

        assertEquals("You do not have access",exception.getMessage());
    }

    @Test
    void testNotFoundUser(){
        when(authentication.getName()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenThrow(new CustomException("User is not found", HttpStatus.NOT_FOUND));
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));

        CustomException customException = assertThrows(CustomException.class, () -> likeService.likePost(authentication, post.getId()));

        assertEquals("User is not found", customException.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, customException.getStatus());
    }
}
