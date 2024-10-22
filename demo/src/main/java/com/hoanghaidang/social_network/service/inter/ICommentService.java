package com.hoanghaidang.social_network.service.inter;

import com.hoanghaidang.social_network.dto.request.AddCommentDto;
import com.hoanghaidang.social_network.dto.request.EditCommentDto;
import com.hoanghaidang.social_network.dto.response.CommentResponse;
import com.hoanghaidang.social_network.entity.Notice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

public interface ICommentService {
    ResponseEntity<Notice> createComment(Authentication authentication, long postId, AddCommentDto addCommentDto);
    ResponseEntity<Notice> deleteComment(Authentication authentication, Long id);
    ResponseEntity<CommentResponse> editComment(Authentication authentication, Long id, EditCommentDto editCommentDto);
}
