package com.hoanghaidang.social_network.service.inter;

import com.hoanghaidang.social_network.dto.request.AddCommentDto;
import com.hoanghaidang.social_network.dto.request.EditCommentDto;
import com.hoanghaidang.social_network.dto.response.ApiResponse;
import com.hoanghaidang.social_network.dto.response.CommentResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

public interface ICommentService {
    ResponseEntity<ApiResponse<CommentResponse>> createComment(Authentication authentication, long postId, AddCommentDto addCommentDto);
    ResponseEntity<ApiResponse<Void>> deleteComment(Authentication authentication, Long id);
    ResponseEntity<ApiResponse<CommentResponse>> editComment(Authentication authentication, Long id, EditCommentDto editCommentDto);
}
