package com.hoanghaidang.social_network.dao;

import com.hoanghaidang.social_network.entity.LikeComment;
import com.hoanghaidang.social_network.entity.LikePost;
import com.hoanghaidang.social_network.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface LikePostRepository extends JpaRepository<LikePost,Long> {
    Page<LikePost> findLikePostByUser(User user, Pageable pageable);
    LikePost findByUserIdAndPostId(Long userId, Long postId);
    int countByUserIdAndCreateAtBetween(long userId, LocalDateTime startDate, LocalDateTime endDate);
}
