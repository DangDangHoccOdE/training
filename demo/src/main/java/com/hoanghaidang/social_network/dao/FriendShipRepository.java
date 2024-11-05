package com.hoanghaidang.social_network.dao;

import com.hoanghaidang.social_network.entity.FriendShip;
import com.hoanghaidang.social_network.entity.User;
import com.hoanghaidang.social_network.enums.FriendStatus;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface FriendShipRepository extends JpaRepository<FriendShip,Long> {
    @Query("SELECT F FROM FriendShip F WHERE (F.user1 = :user or F.user2 = :user) AND F.status = :status")
    Page<FriendShip> findAllByUserAndStatus(@Param("user") User user, @Param("status") FriendStatus status,Pageable pageable);

    @Query("SELECT F FROM FriendShip F WHERE F.user2 = :user AND F.status = :status")
    Page<FriendShip> findAllRequestByUserAndStatus(@Param("user") User user, @Param("status") FriendStatus status,Pageable pageable);

    @Query("SELECT F FROM FriendShip F WHERE F.user1 = :user AND F.status = :status")
    Page<FriendShip> findAllSenRequestByUserAndStatus(@Param("user") User user, @Param("status") FriendStatus status,Pageable pageable);

    // Find the friendship relationship between two users
    Optional<FriendShip> findByUser1AndUser2(User user1, User user2);

    int countByUser1IdAndStatusAndUpdateAtBetween(long userId, FriendStatus status, LocalDateTime startDate, LocalDateTime endDate);
    int countByUser2IdAndStatusAndUpdateAtBetween(long userId, FriendStatus status, LocalDateTime startDate, LocalDateTime endDate);
}
