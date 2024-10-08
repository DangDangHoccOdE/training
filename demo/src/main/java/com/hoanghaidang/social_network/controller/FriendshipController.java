package com.hoanghaidang.social_network.controller;

import com.hoanghaidang.social_network.service.inter.IFriendShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/friend_ship")
public class FriendshipController {
    @Autowired
    private IFriendShipService iFriendShipService;

    @PostMapping("/send_request")
    public ResponseEntity<?> sendFriendRequest(@RequestParam Long senderId,@RequestParam Long receiverId){
        return iFriendShipService.sendFriendRequest(senderId,receiverId);
    }

    @PutMapping("/accept/{friendshipId}")
    public ResponseEntity<?> acceptFriend(@PathVariable("friendshipId") Long friendshipId){
        return iFriendShipService.acceptFriendRequest(friendshipId);
    }

    @PutMapping("/decline/{friendshipId}")
    public ResponseEntity<?> declineFriendship(@PathVariable("friendshipId") Long friendshipId){
        return iFriendShipService.declineFriendShip(friendshipId);
    }

    @DeleteMapping("/delete/{friendshipId}")
    public ResponseEntity<?> deleteFriendship(@PathVariable("friendshipId") Long friendshipId){
        return iFriendShipService.deleteFriendShip(friendshipId);
    }
}
