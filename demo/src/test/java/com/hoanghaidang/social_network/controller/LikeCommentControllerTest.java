package com.hoanghaidang.social_network.controller;

import com.hoanghaidang.social_network.dto.response.ApiResponse;
import com.hoanghaidang.social_network.dto.response.CommentResponse;
import com.hoanghaidang.social_network.dto.response.LikeCommentResponse;
import com.hoanghaidang.social_network.service.impl.LikeCommentService;
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

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class LikeCommentControllerTest {
    @InjectMocks
    private LikeController likeController;
    @Mock
    private LikeCommentService likeCommentService;
    @Mock
    private Authentication authentication;

    private MockMvc mockMvc;

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(likeController).build();
    }

    @Test
    void testLikeCommentList_Success() throws Exception {
        int page = 0;
        int size = 5;

        Map<String, Object> responseMap = new HashMap<>();

        ApiResponse<Map<String,Object>> apiResponse = ApiResponse.<Map<String,Object>>builder()
                .data(responseMap)
                .build();

        when(likeCommentService.getLikeCommentList(authentication, page, size)).thenReturn(ResponseEntity.ok(apiResponse));

        mockMvc.perform(get("/api/like/like_comment_list")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testLikeComment_Success() throws Exception {
        ApiResponse<LikeCommentResponse> apiResponse = ApiResponse.<LikeCommentResponse>builder()
                .message("Like comment is completed")
                .data(new LikeCommentResponse(new CommentResponse()))
                .build();

        when(likeCommentService.likeComment(any(),anyLong())).thenReturn(ResponseEntity.ok(apiResponse));

        mockMvc.perform(post("/api/like/like_comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"commentId\":1}") // gắn vào để map sang được dto
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message",is(apiResponse.getMessage())));
    }

    @Test
    void testUnlike_Success() throws Exception {
        long commentId = 1;
        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .message("Delete like is completed")
                .build();

        when(likeCommentService.unlike(any(),anyLong())).thenReturn(ResponseEntity.ok(apiResponse));

        mockMvc.perform(delete("/api/like/unlike_comment/{commentId}",commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message",is(apiResponse.getMessage())));
    }


}
