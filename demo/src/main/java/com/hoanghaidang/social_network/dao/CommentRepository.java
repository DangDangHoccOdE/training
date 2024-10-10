package com.hoanghaidang.social_network.dao;

import com.hoanghaidang.social_network.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    int countByUserIdAndCreateAtBetween(long userId, LocalDateTime startDate, LocalDateTime endDate);
}
