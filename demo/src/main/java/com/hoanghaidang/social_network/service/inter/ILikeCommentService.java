package com.hoanghaidang.social_network.service.inter;

import com.hoanghaidang.social_network.dto.response.ApiResponse;
import com.hoanghaidang.social_network.dto.response.LikeCommentResponse;
import com.hoanghaidang.social_network.dto.response.LikePostResponse;
import com.hoanghaidang.social_network.service.impl.LikeCommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.Map;

public interface ILikeCommentService {
    ResponseEntity<ApiResponse<LikeCommentResponse>> likeComment(Authentication authentication, long commentId);
    ResponseEntity<ApiResponse<Map<String, Object>>> getLikeCommentList(Authentication authentication, int page, int size);
    ResponseEntity<ApiResponse<Void>> unlike(Authentication authentication,long commentId);
}
