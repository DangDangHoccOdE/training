package com.hoanghaidang.social_network.service.impl;

import com.hoanghaidang.social_network.dao.CommentRepository;
import com.hoanghaidang.social_network.dao.LikeRepository;
import com.hoanghaidang.social_network.dao.PostRepository;
import com.hoanghaidang.social_network.dao.UserRepository;
import com.hoanghaidang.social_network.entity.*;
import com.hoanghaidang.social_network.exception.CustomException;
import com.hoanghaidang.social_network.service.inter.ILikeService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LikeService implements ILikeService {
    LikeRepository likeRepository;
    PostRepository postRepository;
    CommentRepository commentRepository;
    UserRepository userRepository;

    private User getAuthenticatedUser(Authentication authentication) {
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new CustomException("User is not found", HttpStatus.NOT_FOUND));
    }

    private Like checkDuplicateLike(long userId, Long postId, Long commentId) {
        if (postId != null) {
            return likeRepository.findByUserIdAndPostId(userId, postId);
        } else {
            return likeRepository.findByUserIdAndCommentId(userId, commentId);
        }
    }

    @Override
    public ResponseEntity<Notice> likePost(Authentication authentication, long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException("The post is not found", HttpStatus.NOT_FOUND));

        User user = getAuthenticatedUser(authentication);

        if ("Private".equals(post.getStatus()) && user != post.getUser()) {
            throw new CustomException("Post is private, cannot like!", HttpStatus.CONFLICT);
        }

        if (checkDuplicateLike(user.getId(), postId, null) != null) {
            throw new CustomException("Like is duplicate!", HttpStatus.CONFLICT);
        }

        Like like = Like.builder()
                .createAt(LocalDateTime.now())
                .post(post)
                .user(user)
                .build();

        post.setLikeCount(post.getLikeCount() + 1);
        likeRepository.save(like);
        return ResponseEntity.ok(new Notice("Like post is completed"));
    }

    @Override
    public ResponseEntity<Notice> likeComment(Authentication authentication, long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException("The comment is not found", HttpStatus.NOT_FOUND));

        User user = getAuthenticatedUser(authentication);

        if (checkDuplicateLike(user.getId(), null, commentId) != null) {
            throw new CustomException("Like is duplicate!", HttpStatus.CONFLICT);
        }

        Like like = Like.builder()
                .createAt(LocalDateTime.now())
                .comment(comment)
                .user(user)
                .build();

        comment.setLikeCount(comment.getLikeCount() + 1);
        likeRepository.save(like);
        return ResponseEntity.ok(new Notice("Like comment is completed"));
    }

    @Override
    public ResponseEntity<Notice> unlike(Authentication authentication, long likeId) {
        User user = getAuthenticatedUser(authentication);

        Like like = likeRepository.findById(likeId)
                .orElseThrow(() -> new CustomException("Like is not found", HttpStatus.NOT_FOUND));

        if (user != like.getUser()) {
            throw new AccessDeniedException("You do not have access");
        }

        Post post = like.getPost();
        if (post != null) {
            post.setLikeCount(post.getLikeCount() - 1);
        } else {
            Comment comment = like.getComment();
            comment.setLikeCount(comment.getLikeCount() - 1);
        }

        likeRepository.delete(like);
        return ResponseEntity.ok(new Notice("Delete like is completed"));
    }
}

