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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentService implements ICommentService {
    CommentRepository commentRepository;
    UserRepository userRepository;
    PostRepository postRepository;

    @Override
    public ResponseEntity<Notice> createComment(Authentication authentication, long postId, CommentDto commentDto) {
        validateContent(commentDto);

        User user = getUser(authentication);
        Post post = getPost(postId);
        Comment comment = createOrUpdateComment(new Comment(), commentDto, user, post);

        post.setCommentCount(post.getCommentCount() + 1);
        postRepository.save(post);
        commentRepository.save(comment);

        return ResponseEntity.ok(new Notice("Create comment completed"));
    }

    @Override
    public ResponseEntity<CommentDto> editComment(Authentication authentication, Long commentId, CommentDto commentDto) {
        validateContent(commentDto);

        Comment comment = getComment(commentId);
        User user = getUser(authentication);

        validateCommentOwnership(comment, user, commentDto.getPostId());

        createOrUpdateComment(comment, commentDto, user, comment.getPost());
        commentRepository.save(comment);

        return ResponseEntity.ok(commentDto);
    }

    @Override
    public ResponseEntity<Notice> deleteComment(Authentication authentication, Long commentId) {
        Comment comment = getComment(commentId);
        User user = getUser(authentication);

        validateCommentOwnership(comment, user, comment.getPost().getId());

        Post post = comment.getPost();
        post.setCommentCount(post.getCommentCount() - 1);
        postRepository.save(post);

        commentRepository.delete(comment);

        return ResponseEntity.ok(new Notice("Delete comment completed"));
    }

    private void validateContent(CommentDto commentDto) {
        if ((commentDto.getImage() == null || commentDto.getImage().isEmpty()) && commentDto.getContent() == null) {
            throw new CustomException("Images or Content is required", HttpStatus.BAD_REQUEST);
        }
    }

    private User getUser(Authentication authentication) {
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new CustomException("User is not found", HttpStatus.NOT_FOUND));
    }

    private Post getPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new CustomException("Post is not found", HttpStatus.NOT_FOUND));
    }

    private Comment getComment(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException("Comment is not found", HttpStatus.NOT_FOUND));
    }

    private void validateCommentOwnership(Comment comment, User user, Long postId) {
        if (comment.getPost().getId() != postId) {
            throw new CustomException("Comment does not belong to this post", HttpStatus.CONFLICT);
        }
        if (!comment.getUser().equals(user)) {
            throw new AccessDeniedException("You do not have access!");
        }
    }

    private Comment createOrUpdateComment(Comment comment, CommentDto commentDto, User user, Post post) {
        comment.setUser(user);
        comment.setPost(post);
        comment.setContent(commentDto.getContent());
        comment.setImage(commentDto.getImage());
        comment.setUpdateAt(LocalDateTime.now());
        if (comment.getCreateAt() == null) {
            comment.setCreateAt(LocalDateTime.now());
        }
        return comment;
    }
}
