package com.luvina.training_final.SpringBootProject.service.impl;

import com.luvina.training_final.SpringBootProject.dao.CommentRepository;
import com.luvina.training_final.SpringBootProject.dao.PostRepository;
import com.luvina.training_final.SpringBootProject.dao.UserRepository;
import com.luvina.training_final.SpringBootProject.dto.CommentDto;
import com.luvina.training_final.SpringBootProject.entity.Comment;
import com.luvina.training_final.SpringBootProject.entity.Notice;
import com.luvina.training_final.SpringBootProject.entity.Post;
import com.luvina.training_final.SpringBootProject.entity.User;
import com.luvina.training_final.SpringBootProject.exception.CustomException;
import com.luvina.training_final.SpringBootProject.service.inter.ICommentService;
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
