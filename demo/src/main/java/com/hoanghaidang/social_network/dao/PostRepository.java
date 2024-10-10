package com.hoanghaidang.social_network.dao;

import com.hoanghaidang.social_network.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface PostRepository extends JpaRepository<Post,Long> {
    int countByUserIdAndCreateAtBetween(long userId, LocalDateTime createAt, LocalDateTime endDate);
}
