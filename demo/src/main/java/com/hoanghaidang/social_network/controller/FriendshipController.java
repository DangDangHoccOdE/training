package com.hoanghaidang.social_network.controller;

import com.hoanghaidang.social_network.dto.request.FriendshipDto;
import com.hoanghaidang.social_network.dto.response.ApiResponse;
import com.hoanghaidang.social_network.dto.response.FriendshipResponse;
import com.hoanghaidang.social_network.service.inter.IFriendShipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
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
    public ResponseEntity<ApiResponse<FriendshipResponse>> sendFriendRequest(@Validated @RequestBody FriendshipDto friendshipDto, Authentication authentication){
        return iFriendShipService.sendFriendRequest(authentication,friendshipDto.getReceiverId());
    }

    @Operation(summary = "Accept Friendship", description = "Accept Friendship")
    @PutMapping("/accept")
    public ResponseEntity<ApiResponse<FriendshipResponse>> acceptFriend(@Validated @RequestBody FriendshipDto friendshipDto,Authentication authentication){
        return iFriendShipService.acceptFriendRequest(authentication,friendshipDto.getReceiverId());
    }

    @Operation(summary = "Decline Friendship", description = "Decline Friendship")
    @PutMapping("/decline")
    public ResponseEntity<ApiResponse<FriendshipResponse>> declineFriendship(@Validated @RequestBody FriendshipDto friendshipDto, Authentication authentication){
        return iFriendShipService.declineFriendShip(authentication,friendshipDto.getReceiverId());
    }

    @Operation(summary = "Delete Friendship", description = "Delete Friendship")
    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse<Void>> deleteFriendship(@Validated @RequestBody FriendshipDto friendshipDto,Authentication authentication){
        return iFriendShipService.deleteFriendShip(authentication,friendshipDto.getReceiverId());
    }
}
