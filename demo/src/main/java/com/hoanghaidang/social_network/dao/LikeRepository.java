package com.hoanghaidang.social_network.dao;

import com.hoanghaidang.social_network.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Repository
public interface LikeRepository extends JpaRepository<Like,Long> {
    Like findByUserIdAndPostId(Long userId, Long postId);
    Like findByUserIdAndCommentId(Long userId, Long commentId);
    int countByUserIdAndCreateAtBetween(long userId, LocalDateTime startDate, LocalDateTime endDate);
}
