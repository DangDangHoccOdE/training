package com.hoanghaidang.social_network.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hoanghaidang.social_network.dto.request.PostDto;
import com.hoanghaidang.social_network.dto.response.ApiResponse;
import com.hoanghaidang.social_network.dto.response.PostResponse;
import com.hoanghaidang.social_network.entity.Post;
import com.hoanghaidang.social_network.enums.PostStatus;
import com.hoanghaidang.social_network.service.impl.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PostControllerTest {
    @InjectMocks
    private PostController postController;
    @Mock
    private PostService postService;
    @Mock
    private Authentication authentication;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private PostDto postDto;
    private PostResponse postResponse;

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(postController).build();
        objectMapper = new ObjectMapper();

        postDto = PostDto.builder()
                .content("abcd")
                .title("avbc")
                .postStatus(PostStatus.PUBLIC)
                .image(null)
                .build();

        postResponse = PostResponse.builder()
                .content(postDto.getContent())
                .postStatus(postDto.getPostStatus())
                .title(postDto.getTitle())
                .images(postDto.getImage())
                .build();
    }

    @Test
    void testTimeLine_Success() throws Exception {
        String email = "user@example.com";
        int page = 0;
        int size = 10;

        List<Post> posts = new ArrayList<>();
        posts.add(new Post());

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("posts", posts);
        responseMap.put("currentPage", page);
        responseMap.put("totalItems", posts.size());
        responseMap.put("totalPages", 1);

        ApiResponse<Map<String,Object>> apiResponse = ApiResponse.<Map<String,Object>>builder()
                .data(responseMap)
                .build();

        when(postService.timeline(authentication, page, size)).thenReturn(ResponseEntity.ok(apiResponse));

        mockMvc.perform(get("/api/post/timeline")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testCreatePost_Success() throws Exception{
        ApiResponse<PostResponse> apiResponse = ApiResponse.<PostResponse>builder()
                .message("Create post completed!")
                .data(postResponse)
                .build();

        when(postService.createPost(any(),any())).thenReturn(ResponseEntity.ok(apiResponse));

        mockMvc.perform(post("/api/post/create_post")
                .principal(authentication)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message",is(apiResponse.getMessage())));
    }

    @Test
    void testEditPost_Success() throws Exception{
        ApiResponse<PostResponse> apiResponse = ApiResponse.<PostResponse>builder()
                .message("Edit post completed")
                .data(postResponse)
                .build();

        long postId =1;
        when(postService.editPost(any(),anyLong(),any())).thenReturn(ResponseEntity.ok(apiResponse));

        mockMvc.perform(put("/api/post/edit/{postId}",postId)
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postDto)))
                .andExpect(status().isOk());
    }

    @Test
    void testDeletePost_Success() throws Exception {
        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .message("Delete post completed")
                .build();

        long postId = 1;

        when(postService.deletePost(any(),anyLong())).thenReturn(ResponseEntity.ok(apiResponse));

        mockMvc.perform(delete("/api/post/delete/{postId}",postId)
                .contentType(MediaType.APPLICATION_JSON)
                .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message",is(apiResponse.getMessage())));
    }
}
