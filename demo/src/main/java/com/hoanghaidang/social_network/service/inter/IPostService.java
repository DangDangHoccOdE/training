package com.hoanghaidang.social_network.service.inter;

import com.hoanghaidang.social_network.dto.PostDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

public interface IPostService {
    ResponseEntity<?> createPost(Authentication authentication,PostDto postDto);
    ResponseEntity<?> editPost(Authentication authentication,long postId,PostDto postDto);
    ResponseEntity<?> deletePost(Authentication authentication,long postId);
}
