package com.hoanghaidang.social_network.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hoanghaidang.social_network.dto.request.UploadImageResponse;
import com.hoanghaidang.social_network.dto.response.ApiResponse;
import com.hoanghaidang.social_network.entity.Notice;
import com.hoanghaidang.social_network.service.impl.ImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ImageUploadControllerTest {
    @InjectMocks
    private ImageUploadController imageUploadController;
    @Mock
    private Authentication authentication;
    @Mock
    private ImageService imageService;

    private ObjectMapper objectMapper;
    private MockMvc mockMvc;

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(imageUploadController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testUploadImage_Success() throws Exception {
        ApiResponse<UploadImageResponse> apiResponse = ApiResponse.<UploadImageResponse>builder()
                .data(new UploadImageResponse(Arrays.asList("a.png")))
                .build();

        when(imageService.uploadFiles(anyList())).thenReturn(ResponseEntity.ok(apiResponse));

        MockMultipartFile file = new MockMultipartFile("files", "test.jpg", "image/jpeg", "test image".getBytes());

        mockMvc.perform(multipart(HttpMethod.POST,"/api/upload")
                        .file(file)
                .principal(authentication)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    void testGetImage_Success() throws Exception {
        when(imageService.downloadImage(any(String.class)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/api/download").param("filename", "test.jpg"))
                .andExpect(status().isOk());
    }
}
