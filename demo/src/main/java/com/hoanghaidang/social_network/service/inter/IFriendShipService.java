package com.hoanghaidang.social_network.service.inter;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

public interface IFriendShipService {
    ResponseEntity<?> sendFriendRequest(Authentication authentication, Long receiverId);
    ResponseEntity<?> acceptFriendRequest(Authentication authentication,Long friendShipId);
    ResponseEntity<?> declineFriendShip(Authentication authentication,Long friendShipId);
    ResponseEntity<?> deleteFriendShip(Authentication authentication,Long friendShipId);
}
