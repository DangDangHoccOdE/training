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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CommentService implements ICommentService {
    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Override
    public ResponseEntity<?> createComment(CommentDto commentDto) {
        User user = userRepository.findUserById(commentDto.getUserId()).get();
//                .orElseThrow(()->new CustomException("User not found", HttpStatus.NOT_FOUND));

        Post post = postRepository.findById(commentDto.getPostId())
                .orElseThrow(()->new CustomException("Post not found", HttpStatus.NOT_FOUND));

        Comment comment = Comment.builder()
                .createAt(LocalDateTime.now())
                .image(commentDto.getImage())
                .content(commentDto.getContent())
                .user(user)
                .post(post)
                .build();

        commentRepository.save(comment);
        return ResponseEntity.ok(new Notice("Create comment completed"));
    }

    @Override
    public ResponseEntity<?> editComment(Long id,CommentDto commentDto) {
        User user = userRepository.findUserById(commentDto.getUserId()).get();
//                .orElseThrow(()->new CustomException("User not found", HttpStatus.NOT_FOUND));

        Post post = postRepository.findById(commentDto.getPostId())
                .orElseThrow(()->new CustomException("Post not found", HttpStatus.NOT_FOUND));

        Comment comment = commentRepository.findById(id)
                .orElseThrow(()->new CustomException("Comment not found", HttpStatus.NOT_FOUND));

        comment.setContent(commentDto.getContent());
        comment.setImage(commentDto.getImage());
        comment.setUpdateAt(LocalDateTime.now());
        comment.setUser(user);
        comment.setPost(post);

        commentRepository.save(comment);
        return ResponseEntity.ok(new Notice("Edit comment completed"));
    }

    @Override
    public ResponseEntity<?> deleteComment(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(()->new CustomException("Comment not found", HttpStatus.NOT_FOUND));

        commentRepository.delete(comment);
        return ResponseEntity.ok(new Notice("Delete comment completed"));
    }
}
