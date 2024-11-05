package com.hoanghaidang.social_network.service.inter;

import com.hoanghaidang.social_network.dto.response.ApiResponse;
import com.hoanghaidang.social_network.dto.response.FriendshipResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.Map;

public interface IFriendShipService {
    ResponseEntity<ApiResponse<Map<String,Object>>> friendListByUser(Authentication authentication, int page, int size);
    ResponseEntity<ApiResponse<Map<String,Object>>> friendRequestListByUser(Authentication authentication, int page, int size);
    ResponseEntity<ApiResponse<Map<String,Object>>> friendRequestSentListByUser(Authentication authentication, int page, int size);
    ResponseEntity<ApiResponse<Void>> sendFriendRequest(Authentication authentication, Long receiverId);
    ResponseEntity<ApiResponse<Void>> acceptFriendRequest(Authentication authentication,Long receiverId);
    ResponseEntity<ApiResponse<Void>> declineFriendShip(Authentication authentication,Long receiverId);
    ResponseEntity<ApiResponse<Void>> deleteFriendShip(Authentication authentication,Long receiverId);
}
