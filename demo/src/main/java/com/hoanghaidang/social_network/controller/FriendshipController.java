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

import java.util.Map;

@RestController
@RequestMapping("/api/friend_ship")
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@Tag(name = "Friendship Management", description = "APIs for managing friendship")
public class FriendshipController {
    IFriendShipService iFriendShipService;

    @Operation(summary = "Friend List", description = "Friend List")
    @GetMapping("/friend_list")
    public ResponseEntity<ApiResponse<Map<String,Object>>> friendListByUser(Authentication authentication,
                                                                            @RequestParam(defaultValue = "0") int page,
                                                                            @RequestParam(defaultValue = "5") int size){
        return iFriendShipService.friendListByUser(authentication,page,size);
    }

    @Operation(summary = "Friend Request List", description = "Friend Request List")
    @GetMapping("/friend_request_list")
    public ResponseEntity<ApiResponse<Map<String,Object>>> friendRequestListByUser(Authentication authentication,
                                                                            @RequestParam(defaultValue = "0") int page,
                                                                            @RequestParam(defaultValue = "5") int size){
        return iFriendShipService.friendRequestListByUser(authentication,page,size);
    }

    @Operation(summary = "Friend Request Sent List", description = "Friend Request Sent List")
    @GetMapping("/friend_request_sent_list")
    public ResponseEntity<ApiResponse<Map<String,Object>>> friendRequestSentListByUser(Authentication authentication,
                                                                                   @RequestParam(defaultValue = "0") int page,
                                                                                   @RequestParam(defaultValue = "5") int size){
        return iFriendShipService.friendRequestSentListByUser(authentication,page,size);
    }

    @Operation(summary = "Send Friendship", description = "Send Friendship")
    @PostMapping("/send_request")
    public ResponseEntity<ApiResponse<Void>> sendFriendRequest(@Validated @RequestBody FriendshipDto friendshipDto, Authentication authentication){
        return iFriendShipService.sendFriendRequest(authentication,friendshipDto.getReceiverId());
    }

    @Operation(summary = "Accept Friendship", description = "Accept Friendship")
    @PutMapping("/accept")
    public ResponseEntity<ApiResponse<Void>> acceptFriend(@Validated @RequestBody FriendshipDto friendshipDto,Authentication authentication){
        return iFriendShipService.acceptFriendRequest(authentication,friendshipDto.getReceiverId());
    }

    @Operation(summary = "Decline Friendship", description = "Decline Friendship")
    @PutMapping("/decline")
    public ResponseEntity<ApiResponse<Void>> declineFriendship(@Validated @RequestBody FriendshipDto friendshipDto, Authentication authentication){
        return iFriendShipService.declineFriendShip(authentication,friendshipDto.getReceiverId());
    }

    @Operation(summary = "Delete Friendship", description = "Delete Friendship")
    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse<Void>> deleteFriendship(@Validated @RequestBody FriendshipDto friendshipDto,Authentication authentication){
        return iFriendShipService.deleteFriendShip(authentication,friendshipDto.getReceiverId());
    }
}
