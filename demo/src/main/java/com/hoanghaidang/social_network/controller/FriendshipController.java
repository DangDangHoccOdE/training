package com.hoanghaidang.social_network.controller;

import com.hoanghaidang.social_network.service.inter.IFriendShipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/friend_ship")
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@Tag(name = "Friendship Management", description = "APIs for managing friendship")
public class FriendshipController {
    IFriendShipService iFriendShipService;

    @Operation(summary = "Send Friendship", description = "Send Friendship")
    @PostMapping("/send_request")
    public ResponseEntity<?> sendFriendRequest(@RequestParam Long senderId,@RequestParam Long receiverId){
        return iFriendShipService.sendFriendRequest(senderId,receiverId);
    }

    @Operation(summary = "Accept Friendship", description = "Accept Friendship")
    @PutMapping("/accept/{friendshipId}")
    public ResponseEntity<?> acceptFriend(@PathVariable("friendshipId") Long friendshipId){
        return iFriendShipService.acceptFriendRequest(friendshipId);
    }

    @Operation(summary = "Decline Friendship", description = "Decline Friendship")
    @PutMapping("/decline/{friendshipId}")
    public ResponseEntity<?> declineFriendship(@PathVariable("friendshipId") Long friendshipId){
        return iFriendShipService.declineFriendShip(friendshipId);
    }

    @Operation(summary = "Delete Friendship", description = "Delete Friendship")
    @DeleteMapping("/delete/{friendshipId}")
    public ResponseEntity<?> deleteFriendship(@PathVariable("friendshipId") Long friendshipId){
        return iFriendShipService.deleteFriendShip(friendshipId);
    }
}
