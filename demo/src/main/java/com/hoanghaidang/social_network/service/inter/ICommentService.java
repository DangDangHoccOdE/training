package com.hoanghaidang.social_network.service.inter;

import com.hoanghaidang.social_network.dto.CommentDto;
import org.springframework.http.ResponseEntity;

public interface ICommentService {
    ResponseEntity<?> createComment(long userId, long postId,CommentDto commentDto);
    ResponseEntity<?> deleteComment(Long id);
    ResponseEntity<?> editComment(Long id,CommentDto commentDto);
}
