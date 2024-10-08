package com.hoanghaidang.social_network.service.inter;

import org.springframework.http.ResponseEntity;

public interface IFriendShipService {
    ResponseEntity<?> sendFriendRequest(Long senderId, Long receiverId);
    ResponseEntity<?> acceptFriendRequest(Long friendShipId);
    ResponseEntity<?> declineFriendShip(Long friendShipId);
    ResponseEntity<?> deleteFriendShip(Long friendShipId);
}
