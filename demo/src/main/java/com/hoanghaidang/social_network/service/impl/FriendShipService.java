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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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

    @Override
    public ResponseEntity<ApiResponse<Map<String,Object>>> friendListByUser(Authentication authentication,int page,int size) {
        User user = getAuthenticatedUser(authentication);

        // Cùng 1 câu query nhưng có thể tùy chọn sort
        Pageable pageable = PageRequest.of(page, size, Sort.by("updateAt").descending());

        Page<FriendShip> friendShips = friendShipRepository.findAllByUserAndStatus(user,FriendStatus.ACCEPTED, pageable);
        Page<FriendshipResponse> friendshipResponses = friendShips.map(
                friendShip -> friendshipMapper.toFriendship(friendShip,user)
        );

        Map<String, Object> response = new HashMap<>();
        response.put("friends", friendshipResponses.getContent());
        response.put("currentPage", friendshipResponses.getNumber());
        response.put("totalItems", friendshipResponses.getTotalElements());
        response.put("totalPages", friendshipResponses.getTotalPages());

        ApiResponse<Map<String,Object>> apiResponse = ApiResponse.<Map<String,Object>>builder()
                .data(response)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @Override
    public ResponseEntity<ApiResponse<Map<String,Object>>> friendRequestListByUser(Authentication authentication,int page,int size) {
        User user = getAuthenticatedUser(authentication);

        // Cùng 1 câu query nhưng có thể tùy chọn sort
        Pageable pageable = PageRequest.of(page, size, Sort.by("createAt").descending());

        Page<FriendShip> friendShips = friendShipRepository.findAllRequestByUserAndStatus(user,FriendStatus.PENDING, pageable);
        Page<FriendshipResponse> friendshipResponses = friendShips.map(
                friendShip -> friendshipMapper.toFriendship(friendShip,user)
        );

        Map<String, Object> response = new HashMap<>();
        response.put("friends", friendshipResponses.getContent());
        response.put("currentPage", friendshipResponses.getNumber());
        response.put("totalItems", friendshipResponses.getTotalElements());
        response.put("totalPages", friendshipResponses.getTotalPages());

        ApiResponse<Map<String,Object>> apiResponse = ApiResponse.<Map<String,Object>>builder()
                .data(response)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @Override
    public ResponseEntity<ApiResponse<Map<String,Object>>> friendRequestSentListByUser(Authentication authentication,int page,int size) {
        User user = getAuthenticatedUser(authentication);

        // Cùng 1 câu query nhưng có thể tùy chọn sort
        Pageable pageable = PageRequest.of(page, size, Sort.by("createAt").descending());

        Page<FriendShip> friendShips = friendShipRepository.findAllSenRequestByUserAndStatus(user,FriendStatus.PENDING, pageable);
        Page<FriendshipResponse> friendshipResponses = friendShips.map(
                friendShip -> friendshipMapper.toFriendship(friendShip,user)
        );

        Map<String, Object> response = new HashMap<>();
        response.put("friends", friendshipResponses.getContent());
        response.put("currentPage", friendshipResponses.getNumber());
        response.put("totalItems", friendshipResponses.getTotalElements());
        response.put("totalPages", friendshipResponses.getTotalPages());

        ApiResponse<Map<String,Object>> apiResponse = ApiResponse.<Map<String,Object>>builder()
                .data(response)
                .build();
        return ResponseEntity.ok(apiResponse);
    }


    @Override
    public ResponseEntity<ApiResponse<Void>> sendFriendRequest(Authentication authentication, Long receiverId) {
        User sender = getAuthenticatedUser(authentication);
        User receiver = userRepository.findUserById(receiverId)
                .orElseThrow(() -> new CustomException("User is not found", HttpStatus.NOT_FOUND));

        if (sender.equals(receiver)) {
            throw new CustomException("Sender and receiver cannot be the same person", HttpStatus.BAD_REQUEST);
        }

        Optional<FriendShip> friendShip1 = friendShipRepository.findByUser1AndUser2(sender, receiver);
        Optional<FriendShip> friendShip2 = friendShipRepository.findByUser1AndUser2(receiver, sender);

        if ((friendShip1.isPresent() && friendShip1.get().getStatus().equals(FriendStatus.ACCEPTED)) ||
                (friendShip2.isPresent() && friendShip2.get().getStatus().equals(FriendStatus.ACCEPTED))) {
            throw new CustomException("Invitation already accepted!", HttpStatus.BAD_REQUEST);
        }

        if(friendShip1.isPresent() && friendShip1.get().getStatus().equals(FriendStatus.DECLINED)) {
            friendShipRepository.delete(friendShip1.get());
        }else if(friendShip2.isPresent() && friendShip2.get().getStatus().equals(FriendStatus.DECLINED) ) {
            friendShipRepository.delete(friendShip2.get());
        }

        String message = "";
        if(friendShip2.isPresent() && friendShip1.isEmpty()){
            friendShip2.get().setStatus(FriendStatus.ACCEPTED);
            friendShipRepository.save(friendShip2.get());
            message = "Add friend is completed";

        }else {
            FriendShip friendship = new FriendShip();
            friendship.setUser1(sender);
            friendship.setUser2(receiver);
            friendship.setCreateAt(LocalDateTime.now());
            friendship.setStatus(FriendStatus.PENDING);
            friendShipRepository.save(friendship);

            message = "Send add friend is completed";
        }
        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .message(message)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> acceptFriendRequest(Authentication authentication, Long receiverId) {
        User receiver = userRepository.findUserById(receiverId)
                .orElseThrow(() -> new CustomException("User receiver is not found", HttpStatus.NOT_FOUND));
        User auth = getAuthenticatedUser(authentication);

        FriendShip friendShip = findFriendship(auth, receiver);

        checkFriendShipAccess(auth,friendShip);
        checkPendingStatus(friendShip);

        friendShip.setStatus(FriendStatus.ACCEPTED);
        friendShipRepository.save(friendShip);

        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .message("Add friend is completed")
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> declineFriendShip(Authentication authentication, Long receiverId) {
        User receiver = userRepository.findUserById(receiverId)
                .orElseThrow(() -> new CustomException("User receiver is not found", HttpStatus.NOT_FOUND));
        User auth = getAuthenticatedUser(authentication);

        FriendShip friendShip = findFriendship(auth, receiver);

        checkFriendShipAccess(auth,friendShip);
        checkPendingStatus(friendShip);

        friendShip.setStatus(FriendStatus.DECLINED);
        friendShipRepository.save(friendShip);

        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .message("Friendship declined successfully")
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> deleteFriendShip(Authentication authentication, Long receiverId) {
        User receiver = userRepository.findUserById(receiverId)
                .orElseThrow(() -> new CustomException("User receiver is not found", HttpStatus.NOT_FOUND));
        User auth = getAuthenticatedUser(authentication);

        FriendShip friendShip = findFriendship(auth, receiver);

        if (!FriendStatus.ACCEPTED.equals(friendShip.getStatus()) && !FriendStatus.PENDING.equals(friendShip.getStatus())) {
            throw new CustomException("Operation failed, Cannot unfriend", HttpStatus.BAD_REQUEST);
        }

        friendShipRepository.delete(friendShip);

        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .message("Unfriended successfully")
                .build();
        return ResponseEntity.ok(apiResponse);
    }
}


