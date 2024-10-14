package com.hoanghaidang.social_network.service.inter;

import com.hoanghaidang.social_network.entity.Notice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

public interface ILikeService {
    ResponseEntity<Notice> likePost(Authentication authentication, long postId);
    ResponseEntity<Notice> likeComment(Authentication authentication, long commentId);
    ResponseEntity<Notice> unlike(Authentication authentication,long likeId);
}
