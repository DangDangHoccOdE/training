package com.hoanghaidang.social_network.dao;

import com.hoanghaidang.social_network.entity.Post;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface PostRepository extends JpaRepository<Post,Long> {
    int countByUserIdAndCreateAtBetween(long userId, LocalDateTime createAt, LocalDateTime endDate);

    @Query("SELECT p " +
            "FROM Post p " +
            "WHERE " +
            "    (p.user.id = (SELECT u.id FROM User u WHERE u.email = :email))" +
            "    OR (" +
            "        p.user.id IN (" +
            "            SELECT f.user2.id FROM FriendShip f " +
            "            JOIN User u ON f.user1.id = u.id " +
            "            WHERE u.email = :email " +
            "              AND f.status = 'ACCEPTED'" +
            "            UNION" +
            "            SELECT f.user1.id FROM FriendShip f " +
            "            JOIN User u ON f.user2.id = u.id " +
            "            WHERE u.email = :email " +
            "              AND f.status = 'ACCEPTED'" +
            "        ) " +
            "        AND p.postStatus = 'FRIENDS_ONLY'" +
            "    )" +
            "    OR (p.postStatus = 'PUBLIC')")
    Page<Post> findFriendPostsByEmail(@Param("email") String email, Pageable pageable);
}
