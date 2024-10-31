package com.hoanghaidang.social_network.dao;

import com.hoanghaidang.social_network.entity.FriendShip;
import com.hoanghaidang.social_network.entity.User;
import com.hoanghaidang.social_network.enums.FriendStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface FriendShipRepository extends JpaRepository<FriendShip,Long> {
    // Find the friendship relationship between two users
    Optional<FriendShip> findByUser1AndUser2(User user1, User user2);

    int countByUser1IdAndStatusAndUpdateAtBetween(long userId, FriendStatus status, LocalDateTime startDate, LocalDateTime endDate);
    int countByUser2IdAndStatusAndUpdateAtBetween(long userId, FriendStatus status, LocalDateTime startDate, LocalDateTime endDate);
}
