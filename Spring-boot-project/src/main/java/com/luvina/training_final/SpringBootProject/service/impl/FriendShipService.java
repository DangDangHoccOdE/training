package com.luvina.training_final.SpringBootProject.service.impl;

import com.luvina.training_final.SpringBootProject.dao.FriendShipRepository;
import com.luvina.training_final.SpringBootProject.dao.UserRepository;
import com.luvina.training_final.SpringBootProject.entity.FriendShip;
import com.luvina.training_final.SpringBootProject.entity.Notice;
import com.luvina.training_final.SpringBootProject.entity.User;
import com.luvina.training_final.SpringBootProject.exception.CustomException;
import com.luvina.training_final.SpringBootProject.service.inter.IFriendShipService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
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
                .orElseThrow(()->new CustomException("User not found", HttpStatus.NOT_FOUND));
        User receiver = userRepository.findUserById(receiverId)
                .orElseThrow(()-> new CustomException("User not found",HttpStatus.NOT_FOUND));

        Optional<FriendShip> existingFriendship1 = friendShipRepository.findByUser1AndUser2(sender,receiver);
        Optional<FriendShip> existingFriendship2 = friendShipRepository.findByUser1AndUser2(receiver,sender);
        if(existingFriendship2.isPresent()){
            throw new CustomException("Send duplicate invitations!",HttpStatus.BAD_REQUEST);
        } // check xem có bị 2 người gửi kết bạn cho nhau k

        FriendShip friendship = new FriendShip();

        if (existingFriendship1.isPresent()) {
            // Nếu đã từ chối trước đó, cập nhật lại thành pending
            friendship = existingFriendship1.get();
            friendship.setUpdateAt(LocalDateTime.now());
            if (!"declined".equals(friendship.getStatus())) {
                throw new CustomException("Friendship request already exists with status: " + friendship.getStatus(),HttpStatus.BAD_REQUEST);
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
                .orElseThrow(()-> new CustomException("Friendship not found",HttpStatus.NOT_FOUND));

        if(!friendShip.getStatus().equals("pending")){
            throw new CustomException("Operation failed, friend request not found",HttpStatus.NOT_FOUND);
        }

        friendShip.setStatus("accepted");
        friendShipRepository.save(friendShip);
        return ResponseEntity.ok(new Notice("Add friend is completed"));
    }

    @Override
    public ResponseEntity<?> declineFriendShip(Long friendShipId) {
        FriendShip friendShip = friendShipRepository.findById(friendShipId)
                .orElseThrow(()-> new CustomException("Friendship not found",HttpStatus.NOT_FOUND));

        if(!friendShip.getStatus().equals("pending")){
            throw new CustomException("Operation failed, friend request not found",HttpStatus.NOT_FOUND);
        }

        friendShip.setStatus("declined");
        friendShipRepository.save(friendShip);
        return ResponseEntity.ok(new Notice("Unfriending has been completed"));    }

    @Override
    public ResponseEntity<?> deleteFriendShip(Long friendShipId) {
        FriendShip friendShip = friendShipRepository.findById(friendShipId)
                .orElseThrow(()-> new CustomException("Friendship not found",HttpStatus.NOT_FOUND));

        if(!friendShip.getStatus().equals("accepted")){
            throw new CustomException("Operation failed, Cannot unfriend",HttpStatus.BAD_REQUEST);
        }

        friendShipRepository.delete(friendShip);
        return ResponseEntity.ok(new Notice("Unfriended successfully"));
    }
}
