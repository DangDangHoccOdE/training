package com.hoanghaidang.social_network.service.impl;

import com.hoanghaidang.social_network.dao.CommentRepository;
import com.hoanghaidang.social_network.dao.PostRepository;
import com.hoanghaidang.social_network.dao.UserRepository;
import com.hoanghaidang.social_network.dto.CommentDto;
import com.hoanghaidang.social_network.entity.Comment;
import com.hoanghaidang.social_network.entity.Notice;
import com.hoanghaidang.social_network.entity.Post;
import com.hoanghaidang.social_network.entity.User;
import com.hoanghaidang.social_network.exception.CustomException;
import com.hoanghaidang.social_network.service.inter.ICommentService;
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
public class CommentService implements ICommentService {
   CommentRepository commentRepository;
   UserRepository userRepository;
   PostRepository postRepository;

    @Override
    public ResponseEntity<?> createComment(long userId, long postId,CommentDto commentDto) {
        User user = userRepository.findUserById(userId).get();
//                .orElseThrow(()->new CustomException("User not found", HttpStatus.NOT_FOUND));

        Post post = postRepository.findById(postId).get();
//                .orElseThrow(()->new CustomException("Post not found", HttpStatus.NOT_FOUND));

        Comment comment = Comment.builder()
                .createAt(LocalDateTime.now())
                .image(commentDto.getImage())
                .content(commentDto.getContent())
                .user(user)
                .post(post)
                .build();

        post.setCommentCount(post.getCommentCount()+1);

        postRepository.save(post);
        commentRepository.save(comment);
        return ResponseEntity.ok(new Notice("Create comment completed"));
    }

    @Override
    public ResponseEntity<?> editComment(Long commentId,CommentDto commentDto) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(()->new CustomException("Comment not found", HttpStatus.NOT_FOUND));

        comment.setContent(commentDto.getContent());
        comment.setImage(commentDto.getImage());
        comment.setUpdateAt(LocalDateTime.now());

        commentRepository.save(comment);
        return ResponseEntity.ok(commentDto);
    }

    @Override
    public ResponseEntity<?> deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(()->new CustomException("Comment not found", HttpStatus.NOT_FOUND));

        Post post = comment.getPost();
        post.setCommentCount(post.getCommentCount()-1);

        postRepository.save(post);
        commentRepository.delete(comment);
        return ResponseEntity.ok(new Notice("Delete comment completed"));
    }
}
