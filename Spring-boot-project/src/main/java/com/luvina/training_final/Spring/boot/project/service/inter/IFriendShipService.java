package com.luvina.training_final.Spring.boot.project.service.inter;

import org.springframework.http.ResponseEntity;

public interface IFriendShipService {
    ResponseEntity<?> sendFriendRequest(Long senderId, Long receiverId);
    ResponseEntity<?> acceptFriendRequest(Long friendShipId);
    ResponseEntity<?> declineFriendShip(Long friendShipId);
    ResponseEntity<?> deleteFriendShip(Long friendShipId);
}
