package com.hoanghaidang.social_network.service.inter;

import com.hoanghaidang.social_network.entity.Notice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

public interface IFriendShipService {
    ResponseEntity<Notice> sendFriendRequest(Authentication authentication, Long receiverId);
    ResponseEntity<Notice> acceptFriendRequest(Authentication authentication,Long friendShipId);
    ResponseEntity<Notice> declineFriendShip(Authentication authentication,Long friendShipId);
    ResponseEntity<Notice> deleteFriendShip(Authentication authentication,Long friendShipId);
}
