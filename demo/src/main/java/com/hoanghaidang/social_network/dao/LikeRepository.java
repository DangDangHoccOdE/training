package com.hoanghaidang.social_network.dao;

import com.hoanghaidang.social_network.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository extends JpaRepository<Like,Long> {
    Like findByUserIdAndPostId(Long userId, Long postId);
    Like findByUserIdAndCommentId(Long userId, Long commentId);
}
