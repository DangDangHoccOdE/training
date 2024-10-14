package com.hoanghaidang.social_network.service.inter;

import com.hoanghaidang.social_network.dto.CommentDto;
import com.hoanghaidang.social_network.entity.Notice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

public interface ICommentService {
    ResponseEntity<Notice> createComment(Authentication authentication, long postId, CommentDto commentDto);
    ResponseEntity<Notice> deleteComment(Authentication authentication, Long id);
    ResponseEntity<CommentDto> editComment(Authentication authentication,Long id,CommentDto commentDto);
}
