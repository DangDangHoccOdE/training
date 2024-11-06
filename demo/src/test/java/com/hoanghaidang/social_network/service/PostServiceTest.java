package com.hoanghaidang.social_network.service;

import com.hoanghaidang.social_network.dao.FriendShipRepository;
import com.hoanghaidang.social_network.dao.PostRepository;
import com.hoanghaidang.social_network.dao.UserRepository;
import com.hoanghaidang.social_network.dto.request.PostDto;
import com.hoanghaidang.social_network.dto.response.ApiResponse;
import com.hoanghaidang.social_network.dto.response.PostResponse;
import com.hoanghaidang.social_network.entity.FriendShip;
import com.hoanghaidang.social_network.entity.Post;
import com.hoanghaidang.social_network.entity.User;
import com.hoanghaidang.social_network.enums.PostStatus;
import com.hoanghaidang.social_network.exception.CustomException;
import com.hoanghaidang.social_network.mapper.PostMapper;
import com.hoanghaidang.social_network.service.impl.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PostServiceTest {
    @InjectMocks
    private PostService postService;
    @Mock
    private Authentication authentication;
    @Mock
    private PostRepository postRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PostMapper postMapper;
    @Mock
    private Pageable pageable;
    @Mock
    private Page<Post> posts;
    @Mock
    private FriendShipRepository friendShipRepository;

    private User user;
    private PostDto postDto;
    private Post post;
    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
        user = User.builder().id(1L).email("a@gmail.com").build();
        postDto = PostDto.builder().content("a").build();
        post = Post.builder().id(1L).user(user).build();

        Post post2 = Post.builder()
                .user(user)
                .content("abcs")
                .build();
        List<Post> postList = new ArrayList<>();
        postList.add(post2);
        posts = new PageImpl<>(postList);
    }
    private void mockAuthenticationAndUser(User user){
        when(authentication.getName()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
    }

    @Test
    void testGetPostById_PublicPost_Success() {
        mockAuthenticationAndUser(user);
        post.setPostStatus(PostStatus.PUBLIC);

        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));

        PostResponse postResponse =postMapper.toPostResponse(post);
        when(postMapper.toPostResponse(any())).thenReturn(postResponse);

        ResponseEntity<ApiResponse<PostResponse>> response = postService.getPostById(authentication, post.getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testGetPostById_FriendsOnly_Post_AccessGranted() {
        mockAuthenticationAndUser(user);

        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));

        // Mock friendship
        when(friendShipRepository.findByUser1AndUser2(any(), any())).thenReturn(Optional.of(new FriendShip()));

        // Mock the postMapper to return a PostResponse
        PostResponse postResponse = postMapper.toPostResponse(post);
        when(postMapper.toPostResponse(post)).thenReturn(postResponse);

        // Call the service method
        ResponseEntity<ApiResponse<PostResponse>> response = postService.getPostById(authentication, post.getId());

        // Assertions
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testGetPostById_FriendsOnly_Post_AccessDenied() {
        post.setPostStatus(PostStatus.FRIENDS_ONLY);
        post.setUser(User.builder().id(2L).build());

        mockAuthenticationAndUser(user);

        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));

        when(friendShipRepository.findByUser1AndUser2(any(),any())).thenThrow(new AccessDeniedException("You do not have access!"));

        AccessDeniedException exception = assertThrows(AccessDeniedException.class,()->postService.getPostById(authentication,post.getId()));

        assertEquals("You do not have access!",exception.getMessage());
    }

    @Test
    void testGetPostById_PrivatePost_AccessDenied() {
        post.setPostStatus(PostStatus.PRIVATE);
        post.setUser(User.builder().id(2L).build());
        mockAuthenticationAndUser(user);

        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));

        AccessDeniedException exception = assertThrows(AccessDeniedException.class,()->postService.getPostById(authentication,post.getId()));

        assertEquals("You do not have access!",exception.getMessage());
    }

    @Test
    void testGetPostById_PostNotFound() {
        mockAuthenticationAndUser(user);
        when(postRepository.findById(any())).thenThrow(new CustomException("Post could not be found", HttpStatus.NOT_FOUND));

        CustomException exception = assertThrows(CustomException.class,()->postService.getPostById(authentication,post.getId()));

        assertEquals("Post could not be found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND,exception.getStatus());
    }

    @Test
    void testTimeline_Success(){
        int page = 0;
        int size = 5;
        pageable = PageRequest.of(page, size);

        mockAuthenticationAndUser(user);
        when(postRepository.findFriendPostsByEmail(user.getEmail(),pageable)).thenReturn(posts);
        when(postMapper.toPostResponse(any())).thenReturn(new PostResponse());

        ResponseEntity<ApiResponse<Map<String,Object>>> response = postService.timeline(authentication,page,size);

        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertNotNull(response.getBody());

    }

    @Test
    void testCreatePost_Success(){
        mockAuthenticationAndUser(user);

        ResponseEntity<ApiResponse<PostResponse>> response = postService.createPost(authentication,postDto);

        assertEquals("Create post completed!", Objects.requireNonNull(response.getBody()).getMessage());
        assertEquals(HttpStatus.OK,response.getStatusCode());
         verify(postRepository).save(any(Post.class));
    }

    @Test
    void testCreatePost_FailDataIsValid(){
        mockAuthenticationAndUser(user);

        postDto.setContent(null);
        postDto.setTitle(null);
        postDto.setImage(null);

        CustomException exception = assertThrows(CustomException.class,()->postService.createPost(authentication,postDto));

        assertEquals("Post is required a content or a title or images", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST,exception.getStatus());
    }

    @Test
    void testNotFoundUser(){
        when(authentication.getName()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenThrow(new CustomException("User is not found", HttpStatus.NOT_FOUND));

        CustomException exception = assertThrows(CustomException.class,()->postService.createPost(authentication,postDto));

        assertEquals("User is not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND,exception.getStatus());
    }

    @Test
    void testEditPost_Success(){
        mockAuthenticationAndUser(user);
        postDto.setContent("Edit post");

        PostResponse postResponse = PostResponse.builder()
                .content(postDto.getContent())
                .build();

        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        when(postMapper.toPostResponse(any())).thenReturn(postResponse);
        ResponseEntity<ApiResponse<PostResponse>> response = postService.editPost(authentication,post.getId(),postDto);

        assertEquals(response.getStatusCode(),HttpStatus.OK);
        assertEquals(post.getContent(),postResponse.getContent());
        verify(postRepository,times(1)).save(post);
    }

    @Test
    void testEditPost_FailNotFoundPost(){
        mockAuthenticationAndUser(user);
        when(postRepository.findById(post.getId())).thenThrow(new CustomException("Post could not be found",HttpStatus.NOT_FOUND));

        CustomException exception = assertThrows(CustomException.class,()->postService.editPost(authentication,post.getId(),postDto));

        assertEquals(exception.getMessage(),"Post could not be found");
        assertEquals(HttpStatus.NOT_FOUND,exception.getStatus());
    }

    @Test
    void testEditPost_FailInfoEmpty(){
        mockAuthenticationAndUser(user);

        PostDto postDto1 = PostDto.builder().build();

        CustomException exception = assertThrows(CustomException.class,()->postService.editPost(authentication,post.getId(),postDto1));

        assertEquals(exception.getMessage(),"Post is required a content or a title or images");
        assertEquals(HttpStatus.BAD_REQUEST,exception.getStatus());
    }

    @Test
    void testEditPost_FailNotOwner(){
        User other = User.builder().id(2L).email("o@gmail.com").build();
        mockAuthenticationAndUser(user);
        post.setUser(other);

        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        AccessDeniedException exception = assertThrows(AccessDeniedException.class,()->postService.editPost(authentication,post.getId(),postDto));

        assertEquals("You do not have access",exception.getMessage());
    }

    @Test
    void testDeletePost_Success(){
        mockAuthenticationAndUser(user);
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));

        ResponseEntity<ApiResponse<Void>> response = postService.deletePost(authentication,post.getId());

        assertEquals(response.getStatusCode(),HttpStatus.OK);
        assertEquals("Delete post completed", Objects.requireNonNull(response.getBody()).getMessage());
        verify(postRepository,times(1)).delete(post);
    }
}
