package com.hoanghaidang.social_network.service.inter;

import com.hoanghaidang.social_network.dto.response.ApiResponse;
import com.hoanghaidang.social_network.dto.response.FriendshipResponse;
import com.hoanghaidang.social_network.entity.Notice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

public interface IFriendShipService {
    ResponseEntity<ApiResponse<FriendshipResponse>> sendFriendRequest(Authentication authentication, Long receiverId);
    ResponseEntity<ApiResponse<FriendshipResponse>> acceptFriendRequest(Authentication authentication,Long friendShipId);
    ResponseEntity<ApiResponse<FriendshipResponse>> declineFriendShip(Authentication authentication,Long friendShipId);
    ResponseEntity<ApiResponse<Void>> deleteFriendShip(Authentication authentication,Long friendShipId);
}
