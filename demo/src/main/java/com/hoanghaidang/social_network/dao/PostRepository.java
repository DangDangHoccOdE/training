package com.hoanghaidang.social_network.dao;

import com.hoanghaidang.social_network.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface PostRepository extends JpaRepository<Post,Long> {
    int countByUserIdAndCreateAtBetween(long userId, LocalDateTime createAt, LocalDateTime endDate);

    @Query("SELECT p FROM Post p WHERE (p.user.email = :email " +
            "OR p.user IN (" +
            "SELECT f.user2 FROM FriendShip f WHERE f.user1.email = :email AND f.status = 'ACCEPTED') " +
            "OR p.user IN (" +
            "SELECT f.user1 FROM FriendShip f WHERE f.user2.email = :email AND f.status = 'ACCEPTED')) " +
            "AND p.status IN ('Public', 'Friends only')  " +
            "ORDER BY p.createAt DESC")
    Page<Post> findFriendPostsByEmail(String email, Pageable pageable);
}
