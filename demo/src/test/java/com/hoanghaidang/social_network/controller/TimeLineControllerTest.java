package com.hoanghaidang.social_network.controller;

import com.hoanghaidang.social_network.dto.response.ApiResponse;
import com.hoanghaidang.social_network.entity.Post;
import com.hoanghaidang.social_network.service.impl.TimeLineService;
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
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TimeLineControllerTest {
    @InjectMocks
    private TimeLineController timeLineController;
    @Mock
    private TimeLineService timeLineService;
    @Mock
    private Authentication authentication;

    private MockMvc mockMvc;

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(timeLineController).build();
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

        when(timeLineService.timeline(email, page, size)).thenReturn(ResponseEntity.ok(apiResponse));

        mockMvc.perform(get("/api/timeline")
                .principal(authentication)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        }
}
