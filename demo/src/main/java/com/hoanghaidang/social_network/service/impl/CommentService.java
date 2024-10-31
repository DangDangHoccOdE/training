package com.hoanghaidang.social_network.service.impl;

import com.hoanghaidang.social_network.dao.CommentRepository;
import com.hoanghaidang.social_network.dao.PostRepository;
import com.hoanghaidang.social_network.dao.UserRepository;
import com.hoanghaidang.social_network.dto.request.AddCommentDto;
import com.hoanghaidang.social_network.dto.request.EditCommentDto;
import com.hoanghaidang.social_network.dto.response.ApiResponse;
import com.hoanghaidang.social_network.dto.response.CommentResponse;
import com.hoanghaidang.social_network.entity.Comment;
import com.hoanghaidang.social_network.entity.Post;
import com.hoanghaidang.social_network.entity.User;
import com.hoanghaidang.social_network.exception.CustomException;
import com.hoanghaidang.social_network.mapper.CommentMapper;
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
    CommentMapper commentMapper;
    ImageService imageService;

    @Override
    public ResponseEntity<ApiResponse<CommentResponse>> createComment(Authentication authentication, long postId, AddCommentDto addCommentDto) {
        validateContent(addCommentDto);

        User user = getUser(authentication);
        Post post = getPost(postId);
        Comment comment = createComment(new Comment(), addCommentDto, user, post);

        post.setCommentCount(post.getCommentCount() + 1);
        postRepository.save(post);
        commentRepository.save(comment);

        CommentResponse commentResponse = commentMapper.commentResponse(comment);
        ApiResponse<CommentResponse> apiResponse = ApiResponse.<CommentResponse>builder()
                .message("Create comment completed")
                .data(commentResponse)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @Override
    public ResponseEntity<ApiResponse<CommentResponse>> editComment(Authentication authentication, Long commentId, EditCommentDto editCommentDto) {
        validateEditContent(editCommentDto);

        Comment comment = getComment(commentId);
        User user = getUser(authentication);

        validateCommentOwnership(comment,user);

        updateComment(comment, editCommentDto);
        commentRepository.save(comment);

        CommentResponse commentResponse = commentMapper.commentResponse(comment);

        ApiResponse<CommentResponse> apiResponse = ApiResponse.<CommentResponse>builder()
                .message("Edit comment completed")
                .data(commentResponse)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> deleteComment(Authentication authentication, Long commentId) {
        Comment comment = getComment(commentId);
        User user = getUser(authentication);

        validateCommentOwnership(comment, user);

        Post post = comment.getPost();
        post.setCommentCount(post.getCommentCount() - 1);
        postRepository.save(post);

        for(String path : comment.getImage()){
            String sanitizedPath = path.replace("/", "");
            imageService.deleteImageFile(sanitizedPath);
        }
        commentRepository.delete(comment);

        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .message("Delete comment completed")
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    private void validateContent(AddCommentDto addCommentDto) {
        if ((addCommentDto.getImage() == null || addCommentDto.getImage().isEmpty()) && addCommentDto.getContent() == null) {
            throw new CustomException("Images or Content is required", HttpStatus.BAD_REQUEST);
        }
    }

    private void validateEditContent(EditCommentDto editCommentDto) {
        if ((editCommentDto.getImage() == null || editCommentDto.getImage().isEmpty()) && editCommentDto.getContent() == null) {
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

    private void validateCommentOwnership(Comment comment, User user) {
        if (comment.getUser().getId() != user.getId()) {
            throw new AccessDeniedException("You do not have access!");
        }
    }

    private Comment createComment(Comment comment, AddCommentDto addCommentDto, User user, Post post) {
        comment.setUser(user);
        comment.setPost(post);
        comment.setContent(addCommentDto.getContent());

        comment.setImage(addCommentDto.getImage());
        comment.setUpdateAt(LocalDateTime.now());
        if (comment.getCreateAt() == null) {
            comment.setCreateAt(LocalDateTime.now());
        }
        return comment;
    }

    private void updateComment(Comment comment, EditCommentDto editCommentDto) {
        comment.setContent(editCommentDto.getContent());
        comment.setImage(editCommentDto.getImage());
        comment.setUpdateAt(LocalDateTime.now());
    }
}
