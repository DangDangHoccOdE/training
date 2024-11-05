package com.hoanghaidang.social_network.dao;

import com.hoanghaidang.social_network.entity.LikeComment;
import com.hoanghaidang.social_network.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface LikeCommentRepository extends JpaRepository<LikeComment,Long> {
    Page<LikeComment> findLikeCommentByUser(User user, Pageable pageable);
    LikeComment findByUserIdAndCommentId(Long userId, Long commentId);
    int countByUserIdAndCreateAtBetween(long userId, LocalDateTime startDate, LocalDateTime endDate);
}
