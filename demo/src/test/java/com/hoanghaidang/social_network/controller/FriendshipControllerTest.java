package com.hoanghaidang.social_network.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hoanghaidang.social_network.dto.FriendshipDto;
import com.hoanghaidang.social_network.entity.Notice;
import com.hoanghaidang.social_network.entity.User;

import static org.mockito.Mockito.doReturn;

import com.hoanghaidang.social_network.service.impl.FriendShipService;
import static org.hamcrest.CoreMatchers.is;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
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

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(friendshipController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testSendFriendRequest_Success() throws Exception {
        Notice notice = new Notice("Send add friend is completed");

        long receiverId = 1;
        // Mock service để trả về ResponseEntity với thông báo thành công
        doReturn(ResponseEntity.ok(notice)).when(friendShipService).sendFriendRequest(any(), anyLong());

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
        Notice notice = new Notice("Add friend is completed");
        long friendshipId = 1;
        // Mock service để trả về ResponseEntity với thông báo thành công
        doReturn(ResponseEntity.ok(notice)).when(friendShipService).acceptFriendRequest(any(), anyLong());

        // Gọi API và kiểm tra phản hồi
        mockMvc.perform(put("/api/friend_ship/accept/{friendshipId}", friendshipId)
                        .principal(authentication))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Add friend is completed"));
    }

    @Test
    void testDeclineFriendship_Success() throws Exception {
        long friendshipId = 1;
        Notice notice = new Notice("Friendship declined successfully");
        doReturn(ResponseEntity.ok(notice)).when(friendShipService).declineFriendShip(any(),anyLong());

        mockMvc.perform(put("/api/friend_ship/decline/{friendshipId}",friendshipId)
                .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(notice.getMessage()));
    }

    @Test
    void testDeleteFriendship_Success() throws Exception {
        long friendshipId = 1;
        Notice notice = new Notice("Unfriended successfully");
        doReturn(ResponseEntity.ok(notice)).when(friendShipService).deleteFriendShip(any(),anyLong());

        mockMvc.perform(delete("/api/friend_ship/delete/{friendshipId}",friendshipId)
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(notice.getMessage()));
    }

}
