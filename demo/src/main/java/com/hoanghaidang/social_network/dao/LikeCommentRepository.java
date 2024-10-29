package com.hoanghaidang.social_network.dao;

import com.hoanghaidang.social_network.entity.LikeComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface LikeCommentRepository extends JpaRepository<LikeComment,Long> {
    LikeComment findByUserIdAndCommentId(Long userId, Long commentId);
    int countByUserIdAndCreateAtBetween(long userId, LocalDateTime startDate, LocalDateTime endDate);
}
