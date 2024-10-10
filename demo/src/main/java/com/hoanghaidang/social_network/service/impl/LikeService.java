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
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class LikeService implements ILikeService {
    LikeRepository likeRepository;
    PostRepository postRepository;
    CommentRepository commentRepository;
    UserRepository userRepository;

    @Override
    public ResponseEntity<?> likePost(long userId, long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(()->new CustomException("The post in not found", HttpStatus.NOT_FOUND));

        User user = userRepository.findById(userId).get();

        Like like = Like.builder()
                .createAt(LocalDateTime.now())
                .post(post)
                .user(user)
                .build();

        post.setLikeCount(post.getLikeCount()+1);

        likeRepository.save(like);
        return ResponseEntity.ok(new Notice("Like post is completed"));
    }

    @Override
    public ResponseEntity<?> likeComment(long userId, long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(()->new CustomException("The comment in not found", HttpStatus.NOT_FOUND));

        User user = userRepository.findById(userId).get();

        Like like = Like.builder()
                .createAt(LocalDateTime.now())
                .comment(comment)
                .user(user)
                .build();

        comment.setLikeCount(comment.getLikeCount()+1);

        likeRepository.save(like);
        return ResponseEntity.ok(new Notice("Like comment is completed"));
    }

    @Override
    public ResponseEntity<?> deleteLike(long likeId) {
        Like like = likeRepository.findById(likeId).get();

        Post post = like.getPost();

        if(post!=null){
            post.setLikeCount(post.getLikeCount()-1);
        }else{
            Comment comment = like.getComment();
            comment.setLikeCount(comment.getLikeCount()-1);
        }

        likeRepository.delete(like);
        return ResponseEntity.ok(new Notice("Delete like of post is completed"));
    }

//    @Override
//    public ResponseEntity<?> deleteLikeComment(long commentId, long userId) {
//        Like comment = commentRepository.findById(commentId)
//                .orElseThrow(()->new CustomException("The comment in not found", HttpStatus.NOT_FOUND));
//
//        likeRepository.delete(comment);
//        return ResponseEntity.ok(new Notice("Delete like of post is completed"));
//    }
}
