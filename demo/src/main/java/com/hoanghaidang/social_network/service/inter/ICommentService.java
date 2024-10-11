package com.hoanghaidang.social_network.service.inter;

import com.hoanghaidang.social_network.dto.CommentDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

public interface ICommentService {
    ResponseEntity<?> createComment(Authentication authentication, long postId,CommentDto commentDto);
    ResponseEntity<?> deleteComment(Authentication authentication, Long id);
    ResponseEntity<?> editComment(Authentication authentication,Long id,CommentDto commentDto);
}
