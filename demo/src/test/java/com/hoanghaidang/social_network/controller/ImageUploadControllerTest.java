package com.hoanghaidang.social_network.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hoanghaidang.social_network.entity.Notice;
import com.hoanghaidang.social_network.service.impl.ImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

//    @Test
//    void testUploadImage_Success() throws Exception {
//        Notice notice = new Notice("Images uploaded successfully");
//        when(imageService.uploadFiles(anyList())).thenReturn(ResponseEntity.ok(notice));
//
//        MockMultipartFile file = new MockMultipartFile("files", "test.jpg", "image/jpeg", "test image".getBytes());
//
//        mockMvc.perform(post("/api/upload")
//                .principal(authentication)
//                .contentType(MediaType.MULTIPART_FORM_DATA)
//                .content(objectMapper.writeValueAsString(file)))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    public void testDownloadImage_Success() throws Exception {
//        String filename = "test.jpg";
//        when(imageService.downloadImage(filename)).thenReturn(ResponseEntity.ok("File downloaded successfully"));
//
//        mockMvc.perform(get("/api/images/download/{filename}", filename))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$").value("File downloaded successfully"));
//    }
}
