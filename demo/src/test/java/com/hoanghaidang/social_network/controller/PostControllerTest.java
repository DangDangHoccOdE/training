package com.hoanghaidang.social_network.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hoanghaidang.social_network.dto.request.PostDto;
import com.hoanghaidang.social_network.dto.response.PostResponse;
import com.hoanghaidang.social_network.entity.Notice;
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
                .status("Public")
                .image(null)
                .build();

        postResponse = PostResponse.builder()
                .content(postDto.getContent())
                .status(postDto.getStatus())
                .title(postDto.getTitle())
                .images(postDto.getImage())
                .build();
    }

    @Test
    void testCreatePost_Success() throws Exception{
        Notice notice = new Notice("Create post completed!");
        when(postService.createPost(any(),any())).thenReturn(ResponseEntity.ok(notice));

        mockMvc.perform(post("/api/post/create_post")
                .principal(authentication)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message",is(notice.getMessage())));
    }

    @Test
    void testEditPost_Success() throws Exception{
        long postId =1;
        when(postService.editPost(any(),anyLong(),any())).thenReturn(ResponseEntity.ok(postResponse));

        mockMvc.perform(put("/api/post/edit/{postId}",postId)
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postDto)))
                .andExpect(status().isOk());
    }

    @Test
    void testDeletePost_Success() throws Exception {
        long postId = 1;

        Notice notice = new Notice("Delete post completed");
        when(postService.deletePost(any(),anyLong())).thenReturn(ResponseEntity.ok(notice));

        mockMvc.perform(delete("/api/post/delete/{postId}",postId)
                .contentType(MediaType.APPLICATION_JSON)
                .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message",is(notice.getMessage())));
    }
}
