package com.hoanghaidang.social_network.service.impl;

import com.hoanghaidang.social_network.dao.FriendShipRepository;
import com.hoanghaidang.social_network.dao.UserRepository;
import com.hoanghaidang.social_network.entity.FriendShip;
import com.hoanghaidang.social_network.entity.Notice;
import com.hoanghaidang.social_network.entity.User;
import com.hoanghaidang.social_network.exception.CustomException;
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
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FriendShipService implements IFriendShipService {
    FriendShipRepository friendShipRepository;
    UserRepository userRepository;

    private User getAuthenticatedUser(Authentication authentication) {
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new CustomException("User is not found", HttpStatus.NOT_FOUND));
    }

    private FriendShip getFriendShip(Long friendShipId) {
        return friendShipRepository.findById(friendShipId)
                .orElseThrow(() -> new CustomException("Friendship is not found", HttpStatus.NOT_FOUND));
    }

    private void checkFriendShipAccess(User user, FriendShip friendShip) {
        if (user != friendShip.getUser2()) {
            throw new AccessDeniedException("You do have not access");
        }
    }

    private void checkPendingStatus(FriendShip friendShip) {
        if (!"pending".equals(friendShip.getStatus())) {
            throw new CustomException("Operation failed, friend request not found", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<Notice> sendFriendRequest(Authentication authentication, Long receiverId) {
        User sender = getAuthenticatedUser(authentication);
        User receiver = userRepository.findUserById(receiverId)
                .orElseThrow(() -> new CustomException("User is not found", HttpStatus.NOT_FOUND));

        if (sender == receiver) {
            throw new CustomException("Sender and receiver cannot be the same person", HttpStatus.BAD_REQUEST);
        }

        Optional<FriendShip> existingFriendship1 = friendShipRepository.findByUser1AndUser2(sender, receiver);
        Optional<FriendShip> existingFriendship2 = friendShipRepository.findByUser1AndUser2(receiver, sender);

        if ((existingFriendship2.isPresent() && existingFriendship2.get().getStatus().equals("pending"))
            || (existingFriendship1.isPresent() && existingFriendship1.get().getStatus().equals("pending"))
        ) {
            throw new CustomException("Send duplicate invitations!", HttpStatus.BAD_REQUEST);
        }

        FriendShip friendship = existingFriendship1.orElse(new FriendShip());

        if (existingFriendship1.isPresent() && "declined".equals(friendship.getStatus())) {
            friendship.setUpdateAt(LocalDateTime.now());
        } else {
            friendship.setUser1(sender);
            friendship.setUser2(receiver);
            friendship.setCreateAt(LocalDateTime.now());
        }

        friendship.setStatus("pending");
        friendShipRepository.save(friendship);
        return ResponseEntity.ok(new Notice("Send add friend is completed"));
    }

    @Override
    public ResponseEntity<Notice> acceptFriendRequest(Authentication authentication, Long friendShipId) {
        FriendShip friendShip = getFriendShip(friendShipId);
        User auth = getAuthenticatedUser(authentication);
        checkFriendShipAccess(auth, friendShip);
        checkPendingStatus(friendShip);

        friendShip.setStatus("accepted");
        friendShipRepository.save(friendShip);
        return ResponseEntity.ok(new Notice("Add friend is completed"));
    }

    @Override
    public ResponseEntity<Notice> declineFriendShip(Authentication authentication, Long friendShipId) {
        FriendShip friendShip = getFriendShip(friendShipId);
        User auth = getAuthenticatedUser(authentication);
        checkFriendShipAccess(auth, friendShip);
        checkPendingStatus(friendShip);

        friendShip.setStatus("declined");
        friendShipRepository.save(friendShip);
        return ResponseEntity.ok(new Notice("Friendship declined successfully"));
    }

    @Override
    public ResponseEntity<Notice> deleteFriendShip(Authentication authentication, Long friendShipId) {
        FriendShip friendShip = getFriendShip(friendShipId);
        User auth = getAuthenticatedUser(authentication);

        if (!"accepted".equals(friendShip.getStatus())) {
            throw new CustomException("Operation failed, Cannot unfriend", HttpStatus.BAD_REQUEST);
        }

        if (auth != friendShip.getUser2() && auth != friendShip.getUser1()) {
            throw new AccessDeniedException("You do have not access");
        }

        friendShipRepository.delete(friendShip);
        return ResponseEntity.ok(new Notice("Unfriended successfully"));
    }
}

