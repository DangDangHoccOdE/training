package com.hoanghaidang.social_network.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hoanghaidang.social_network.dto.request.AddCommentDto;
import com.hoanghaidang.social_network.dto.response.ApiResponse;
import com.hoanghaidang.social_network.dto.response.CommentResponse;
import com.hoanghaidang.social_network.entity.Notice;
import com.hoanghaidang.social_network.service.impl.CommentService;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CommentControllerTest {

    @InjectMocks
    private CommentController commentController;

    @Mock
    private CommentService commentService;

    @Mock
    private Authentication authentication;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    private AddCommentDto addCommentDto;
    private CommentResponse commentResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(commentController).build();
        objectMapper = new ObjectMapper();

        addCommentDto = AddCommentDto.builder()
                .postId(1L)
                .content("Test comment")
                .image(null)
                .build();

        commentResponse = CommentResponse.builder()
                .content(addCommentDto.getContent())
                .build();
    }

    @Test
    void createComment_ShouldReturnNotice_WhenSuccessful() throws Exception {
        ApiResponse<CommentResponse> apiResponse = ApiResponse.<CommentResponse>builder()
                .message("Create comment completed")
                .data(commentResponse)
                .build();

        when(commentService.createComment(any(), anyLong(), any())).thenReturn(ResponseEntity.ok(apiResponse));

        mockMvc.perform(post("/api/comment/comment_post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addCommentDto))
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is(apiResponse.getMessage())));
    }

    @Test
    void editComment_ShouldReturnCommentResponse_WhenSuccessful() throws Exception {
        ApiResponse<CommentResponse> apiResponse = ApiResponse.<CommentResponse>builder()
                .message("Edit comment completed")
                .data(commentResponse)
                .build();
        long commentId = 1;
        when(commentService.editComment(any(), anyLong(), any()))
                .thenReturn(ResponseEntity.ok(apiResponse));

        mockMvc.perform(put("/api/comment/edit/{id}", commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addCommentDto))
                        .principal(authentication))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is(apiResponse.getMessage())));
    }

//    @Test
//    void editComment_ShouldThrowException_WhenCommentNotFound() throws Exception {
//        // Giả lập việc ném CustomException khi comment không tồn tại
//        doThrow(new CustomException("Comment is not found",HttpStatus.NOT_FOUND))
//                .when(commentService).editComment(any(), anyLong(), any());
//
//
//        // Thực hiện request và kiểm tra kết quả
//        mockMvc.perform(put("/api/comment/edit/{id}", 1L)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(commentDto))
//                        .principal(authentication))
//                .andExpect(status().isNotFound())
//                .andExpect(jsonPath("$.message", is("Comment is not found")));
//    }

    @Test
    void deleteComment_ShouldReturnNotice_WhenSuccessful() throws Exception {
        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .message("Delete comment completed")
                .build();
        long commentId = 1;
        when(commentService.deleteComment(authentication, commentId)).thenReturn(ResponseEntity.ok(apiResponse));

        mockMvc.perform(delete("/api/comment/delete/{id}", commentId)
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is(apiResponse.getMessage())));
    }

//    @Test
//    void deleteComment_ShouldThrowException_WhenCommentNotFound() throws Exception {
//        long commentId = 1;
//        when(commentService.deleteComment(authentication, commentId))
//                .thenThrow(new CustomException("Comment is not found", HttpStatus.NOT_FOUND));
//
//        mockMvc.perform(delete("/api/comment/delete/{id}", commentId)
//                        .principal(authentication))
//                .andExpect(status().isNotFound());
//    }
}
