package com.hoanghaidang.social_network.service.inter;

import org.springframework.http.ResponseEntity;

public interface ILikeService {
    ResponseEntity<?> likePost(long userId, long postId);
    ResponseEntity<?> likeComment(long userId, long commentId);
    ResponseEntity<?> deleteLike(long likeId);
//    ResponseEntity<?> deleteLikeComment(long likeId, long userId);

}