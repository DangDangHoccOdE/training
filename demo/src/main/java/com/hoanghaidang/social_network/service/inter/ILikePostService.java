package com.hoanghaidang.social_network.service.inter;

import com.hoanghaidang.social_network.dto.response.ApiResponse;
import com.hoanghaidang.social_network.dto.response.LikeCommentResponse;
import com.hoanghaidang.social_network.dto.response.LikePostResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.Map;

public interface ILikePostService {
    ResponseEntity<ApiResponse<Map<String, Object>>> getLikePostList(Authentication authentication, int page, int size);
    ResponseEntity<ApiResponse<LikePostResponse>> likePost(Authentication authentication, long postId);
    ResponseEntity<ApiResponse<Void>> unlike(Authentication authentication,long postId);

}
