package com.hoanghaidang.social_network.service.inter;

import com.hoanghaidang.social_network.dto.request.PostDto;
import com.hoanghaidang.social_network.dto.response.ApiResponse;
import com.hoanghaidang.social_network.dto.response.PostResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.Map;

public interface IPostService {
    ResponseEntity<ApiResponse<Map<String,Object>>> timeline(Authentication authentication, int page, int size);
    ResponseEntity<ApiResponse<PostResponse>> getPostById(Authentication authentication,long postId);
    ResponseEntity<ApiResponse<PostResponse>> createPost(Authentication authentication, PostDto postDto);
    ResponseEntity<ApiResponse<PostResponse>> editPost(Authentication authentication, long postId, PostDto postDto);
    ResponseEntity<ApiResponse<Void>> deletePost(Authentication authentication,long postId);
}
