package com.luvina.training_final.Spring.boot.project.service.impl;

import com.luvina.training_final.Spring.boot.project.dao.FriendShipRepository;
import com.luvina.training_final.Spring.boot.project.dao.UserRepository;
import com.luvina.training_final.Spring.boot.project.entity.FriendShip;
import com.luvina.training_final.Spring.boot.project.entity.Notice;
import com.luvina.training_final.Spring.boot.project.entity.User;
import com.luvina.training_final.Spring.boot.project.exception.BadRequestException;
import com.luvina.training_final.Spring.boot.project.exception.NotFoundException;
import com.luvina.training_final.Spring.boot.project.service.inter.IFriendShipService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class FriendShipService implements IFriendShipService {
    FriendShipRepository friendShipRepository;
    UserRepository userRepository;

    @Override
    public ResponseEntity<?> sendFriendRequest(Long senderId, Long receiverId) {
        User sender = userRepository.findUserById(senderId)
                .orElseThrow(()->new NotFoundException("User not found"));
        User receiver = userRepository.findUserById(receiverId)
                .orElseThrow(()-> new NotFoundException("User not found"));

        Optional<FriendShip> existingFriendship = friendShipRepository.findByUser1AndUser2(sender,receiver);
        FriendShip friendship = new FriendShip();

        if (existingFriendship.isPresent()) {
            // Nếu đã từ chối trước đó, cập nhật lại thành pending
            friendship = existingFriendship.get();
            friendship.setUpdateAt(LocalDateTime.now());
            if (!"declined".equals(friendship.getStatus())) {
                throw new BadRequestException("Friendship request already exists with status: " + friendship.getStatus());
            }
        } else {
            // Nếu chưa có mối quan hệ nào, tạo lời mời mới
            friendship.setUser1(sender);
            friendship.setUser2(receiver);
            friendship.setCreateAt(LocalDateTime.now());
        }

        friendship.setStatus("pending");
        friendShipRepository.save(friendship);
        return ResponseEntity.ok(new Notice("Send add friend is completed"));
    }

    @Override
    public ResponseEntity<?> acceptFriendRequest(Long friendShipId) {
        FriendShip friendShip = friendShipRepository.findById(friendShipId)
                .orElseThrow(()-> new NotFoundException("Friendship not found"));

        if(!friendShip.getStatus().equals("pending")){
            throw new BadRequestException("Operation failed, friend request not found");
        }

        friendShip.setStatus("accepted");
        friendShipRepository.save(friendShip);
        return ResponseEntity.ok(new Notice("Add friend is completed"));
    }

    @Override
    public ResponseEntity<?> declineFriendShip(Long friendShipId) {
        FriendShip friendShip = friendShipRepository.findById(friendShipId)
                .orElseThrow(()-> new NotFoundException("Friendship not found"));

        if(!friendShip.getStatus().equals("pending")){
            throw new BadRequestException("Operation failed, friend request not found");
        }

        friendShip.setStatus("declined");
        friendShipRepository.save(friendShip);
        return ResponseEntity.ok(new Notice("Unfriending has been completed"));    }

    @Override
    public ResponseEntity<?> deleteFriendShip(Long friendShipId) {
        FriendShip friendShip = friendShipRepository.findById(friendShipId)
                .orElseThrow(()-> new NotFoundException("Friendship not found"));

        if(!friendShip.getStatus().equals("accepted")){
            throw new BadRequestException("Operation failed, Cannot unfriend");
        }

        friendShipRepository.delete(friendShip);
        return ResponseEntity.ok(new Notice("Unfriended successfully"));
    }
}
