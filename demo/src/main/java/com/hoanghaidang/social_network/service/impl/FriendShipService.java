package com.hoanghaidang.social_network.service.impl;

import com.hoanghaidang.social_network.dao.FriendShipRepository;
import com.hoanghaidang.social_network.dao.UserRepository;
import com.hoanghaidang.social_network.dto.response.ApiResponse;
import com.hoanghaidang.social_network.dto.response.FriendshipResponse;
import com.hoanghaidang.social_network.entity.FriendShip;
import com.hoanghaidang.social_network.enums.FriendStatus;
import com.hoanghaidang.social_network.entity.User;
import com.hoanghaidang.social_network.exception.CustomException;
import com.hoanghaidang.social_network.mapper.FriendshipMapper;
import com.hoanghaidang.social_network.service.inter.IFriendShipService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FriendShipService implements IFriendShipService {
    FriendShipRepository friendShipRepository;
    UserRepository userRepository;
    FriendshipMapper friendshipMapper;

    private User getAuthenticatedUser(Authentication authentication) {
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new CustomException("User is not found", HttpStatus.NOT_FOUND));
    }

    private FriendShip findFriendship(User user1, User user2) {
        return friendShipRepository.findByUser1AndUser2(user1, user2)
                .or(() -> friendShipRepository.findByUser1AndUser2(user2, user1))
                .orElseThrow(() -> new CustomException("Friendship is not found", HttpStatus.NOT_FOUND));
    }

    private void checkFriendShipAccess(User auth, FriendShip friendShip) {
        if(friendShip.getUser2() != auth){
            throw new AccessDeniedException("You do have not access");
        }
    }

    private void checkPendingStatus(FriendShip friendShip) {
        if (!FriendStatus.PENDING.equals(friendShip.getStatus())) {
            throw new CustomException("Operation failed, friend request not found", HttpStatus.BAD_REQUEST);
        }
    }

    private void validateNonDuplicateFriendRequest(User sender, User receiver) {
        if (friendShipRepository.findByUser1AndUser2(sender, receiver).isPresent() ||
                friendShipRepository.findByUser1AndUser2(receiver, sender).isPresent()) {
            throw new CustomException("Send duplicate invitations!", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<ApiResponse<FriendshipResponse>> sendFriendRequest(Authentication authentication, Long receiverId) {
        User sender = getAuthenticatedUser(authentication);
        User receiver = userRepository.findUserById(receiverId)
                .orElseThrow(() -> new CustomException("User is not found", HttpStatus.NOT_FOUND));

        if (sender.equals(receiver)) {
            throw new CustomException("Sender and receiver cannot be the same person", HttpStatus.BAD_REQUEST);
        }

        validateNonDuplicateFriendRequest(sender, receiver);

        FriendShip friendship = new FriendShip();
        friendship.setUser1(sender);
        friendship.setUser2(receiver);
        friendship.setCreateAt(LocalDateTime.now());
        friendship.setStatus(FriendStatus.PENDING);
        friendShipRepository.save(friendship);

        FriendshipResponse friendshipResponse = friendshipMapper.toFriendship(friendship);
        ApiResponse<FriendshipResponse> apiResponse = ApiResponse.<FriendshipResponse>builder()
                .message("Send add friend is completed")
                .data(friendshipResponse)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @Override
    public ResponseEntity<ApiResponse<FriendshipResponse>> acceptFriendRequest(Authentication authentication, Long receiverId) {
        User receiver = userRepository.findUserById(receiverId)
                .orElseThrow(() -> new CustomException("User receiver is not found", HttpStatus.NOT_FOUND));
        User auth = getAuthenticatedUser(authentication);

        FriendShip friendShip = findFriendship(auth, receiver);

        checkFriendShipAccess(auth,friendShip);
        checkPendingStatus(friendShip);

        friendShip.setStatus(FriendStatus.ACCEPTED);
        friendShipRepository.save(friendShip);

        FriendshipResponse friendshipResponse = friendshipMapper.toFriendship(friendShip);
        ApiResponse<FriendshipResponse> apiResponse = ApiResponse.<FriendshipResponse>builder()
                .message("Add friend is completed")
                .data(friendshipResponse)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @Override
    public ResponseEntity<ApiResponse<FriendshipResponse>> declineFriendShip(Authentication authentication, Long receiverId) {
        User receiver = userRepository.findUserById(receiverId)
                .orElseThrow(() -> new CustomException("User receiver is not found", HttpStatus.NOT_FOUND));
        User auth = getAuthenticatedUser(authentication);

        FriendShip friendShip = findFriendship(auth, receiver);

        checkFriendShipAccess(auth,friendShip);
        checkPendingStatus(friendShip);

        friendShip.setStatus(FriendStatus.DECLINED);
        friendShipRepository.save(friendShip);

        FriendshipResponse friendshipResponse = friendshipMapper.toFriendship(friendShip);
        ApiResponse<FriendshipResponse> apiResponse = ApiResponse.<FriendshipResponse>builder()
                .message("Friendship declined successfully")
                .data(friendshipResponse)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> deleteFriendShip(Authentication authentication, Long receiverId) {
        User receiver = userRepository.findUserById(receiverId)
                .orElseThrow(() -> new CustomException("User receiver is not found", HttpStatus.NOT_FOUND));
        User auth = getAuthenticatedUser(authentication);

        FriendShip friendShip = findFriendship(auth, receiver);

        if (!FriendStatus.ACCEPTED.equals(friendShip.getStatus())) {
            throw new CustomException("Operation failed, Cannot unfriend", HttpStatus.BAD_REQUEST);
        }

        friendShipRepository.delete(friendShip);

        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .message("Unfriended successfully")
                .build();
        return ResponseEntity.ok(apiResponse);
    }
}


