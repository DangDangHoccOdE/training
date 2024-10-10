package com.hoanghaidang.social_network.dao;

import com.hoanghaidang.social_network.entity.FriendShip;
import com.hoanghaidang.social_network.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FriendShipRepository extends JpaRepository<FriendShip,Long> {
    // Find the friendship relationship between two users
    Optional<FriendShip> findByUser1AndUser2(User user1, User user2);
}