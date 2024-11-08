package com.hoanghaidang.social_network.controller;

import com.hoanghaidang.social_network.dto.response.ApiResponse;
import static org.hamcrest.CoreMatchers.is;

import com.hoanghaidang.social_network.dto.response.LikePostResponse;
import com.hoanghaidang.social_network.dto.response.PostResponse;
import com.hoanghaidang.social_network.entity.Post;
import com.hoanghaidang.social_network.service.impl.LikePostService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class LikePostControllerTest {
    @InjectMocks
    private LikeController likeController;
    @Mock
    private LikePostService likePostService;
    @Mock
    private Authentication authentication;

    private MockMvc mockMvc;

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(likeController).build();
    }

    @Test
    void testLikePostList_Success() throws Exception {
        int page = 0;
        int size = 5;

        Map<String, Object> responseMap = new HashMap<>();

        ApiResponse<Map<String,Object>> apiResponse = ApiResponse.<Map<String,Object>>builder()
                .data(responseMap)
                .build();

        when(likePostService.getLikePostList(authentication, page, size)).thenReturn(ResponseEntity.ok(apiResponse));

        mockMvc.perform(get("/api/like/like_post_list")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testLikePost_Success() throws Exception {
        ApiResponse<LikePostResponse> apiResponse = ApiResponse.<LikePostResponse>builder()
                .message("Like post is completed")
                .data(new LikePostResponse(new PostResponse()))
                .build();

        when(likePostService.likePost(any(),anyLong())).thenReturn(ResponseEntity.ok(apiResponse));

        mockMvc.perform(post("/api/like/like_post")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"postId\":1}") // gắn vào để map sang được dto
                .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message",is(apiResponse.getMessage())));
    }

    @Test
    void testUnlike_Success() throws Exception {
        long postId = 1;
        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .message("Delete like is completed")
                .build();

        when(likePostService.unlike(any(),anyLong())).thenReturn(ResponseEntity.ok(apiResponse));

        mockMvc.perform(delete("/api/like/unlike_post/{postId}",postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message",is(apiResponse.getMessage())));
    }
}
