package com.hoanghaidang.social_network.service.inter;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

public interface ILikeService {
    ResponseEntity<?> likePost(Authentication authentication, long postId);
    ResponseEntity<?> likeComment(Authentication authentication, long commentId);
    ResponseEntity<?> unlike(Authentication authentication,long likeId);
}
