package com.hoanghaidang.social_network.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hoanghaidang.social_network.dto.response.ApiResponse;

import static org.mockito.Mockito.doReturn;

import com.hoanghaidang.social_network.service.impl.FriendShipService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class FriendshipControllerTest {
    @InjectMocks
    private FriendshipController friendshipController;

    @Mock
    private Authentication authentication;
    @Mock
    private FriendShipService friendShipService;

    private ObjectMapper objectMapper;
    private MockMvc mockMvc;
    private final long receiverId= 1;

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(friendshipController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testFriendListByUser_Success() throws Exception {
        int page = 0;
        int size = 5;

        Map<String, Object> responseMap = new HashMap<>();

        ApiResponse<Map<String,Object>> apiResponse = ApiResponse.<Map<String,Object>>builder()
                .data(responseMap)
                .build();

        when(friendShipService.friendListByUser(authentication, page, size)).thenReturn(ResponseEntity.ok(apiResponse));

        mockMvc.perform(get("/api/friend_ship/friend_list")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testFriendRequestListByUser_Success() throws Exception {
        int page = 0;
        int size = 5;

        Map<String, Object> responseMap = new HashMap<>();

        ApiResponse<Map<String,Object>> apiResponse = ApiResponse.<Map<String,Object>>builder()
                .data(responseMap)
                .build();

        when(friendShipService.friendRequestListByUser(authentication, page, size)).thenReturn(ResponseEntity.ok(apiResponse));

        mockMvc.perform(get("/api/friend_ship/friend_request_list")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testFriendRequestSentListByUser_Success() throws Exception {
        int page = 0;
        int size = 5;

        Map<String, Object> responseMap = new HashMap<>();

        ApiResponse<Map<String,Object>> apiResponse = ApiResponse.<Map<String,Object>>builder()
                .data(responseMap)
                .build();

        when(friendShipService.friendRequestSentListByUser(authentication, page, size)).thenReturn(ResponseEntity.ok(apiResponse));

        mockMvc.perform(get("/api/friend_ship/friend_request_sent_list")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testSendFriendRequest_Success() throws Exception {
        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .message("Send add friend is completed")
                .build();

        // Mock service để trả về ResponseEntity với thông báo thành công
        doReturn(ResponseEntity.ok(apiResponse)).when(friendShipService).sendFriendRequest(any(), anyLong());

        // Gọi API và kiểm tra phản hồi
        mockMvc.perform(post("/api/friend_ship/send_request")
                        .content(objectMapper.writeValueAsString(receiverId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(authentication))
                .andDo(print())  // In kết quả ra console
                .andExpect(status().isOk())  // Kiểm tra HTTP status là 200
                .andExpect(jsonPath("$.message").value("Send add friend is completed"));  // Kiểm tra nội dung JSON trả về
    }


    @Test
    void testAcceptFriendshipRequest_Success() throws Exception {
        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .message("Add friend is completed")
                .build();
        // Mock service để trả về ResponseEntity với thông báo thành công
        doReturn(ResponseEntity.ok(apiResponse)).when(friendShipService).acceptFriendRequest(any(), anyLong());

        // Gọi API và kiểm tra phản hồi
        mockMvc.perform(put("/api/friend_ship/accept")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(receiverId)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Add friend is completed"));
    }

    @Test
    void testDeclineFriendship_Success() throws Exception {
        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .message("Friendship declined successfully")
                .build();
        doReturn(ResponseEntity.ok(apiResponse)).when(friendShipService).declineFriendShip(any(),anyLong());

        mockMvc.perform(put("/api/friend_ship/decline")
                .principal(authentication)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(receiverId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(apiResponse.getMessage()));
    }

    @Test
    void testDeleteFriendship_Success() throws Exception {
        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .message("Unfriended successfully")
                .build();

        doReturn(ResponseEntity.ok(apiResponse)).when(friendShipService).deleteFriendShip(any(),anyLong());

        mockMvc.perform(delete("/api/friend_ship/delete")
                        .principal(authentication)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(receiverId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(apiResponse.getMessage()));
    }

}
