package com.hoanghaidang.social_network.service.inter;

import com.hoanghaidang.social_network.dto.request.PostDto;
import com.hoanghaidang.social_network.dto.response.ApiResponse;
import com.hoanghaidang.social_network.dto.response.PostResponse;
import com.hoanghaidang.social_network.entity.Notice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

public interface IPostService {
    ResponseEntity<ApiResponse<PostResponse>> createPost(Authentication authentication, PostDto postDto);
    ResponseEntity<ApiResponse<PostResponse>> editPost(Authentication authentication, long postId, PostDto postDto);
    ResponseEntity<ApiResponse<Void>> deletePost(Authentication authentication,long postId);
}
