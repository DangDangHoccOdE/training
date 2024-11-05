package com.hoanghaidang.social_network.service.impl;

import com.hoanghaidang.social_network.dao.*;
import com.hoanghaidang.social_network.dto.response.ApiResponse;
import com.hoanghaidang.social_network.dto.response.LikeCommentResponse;
import com.hoanghaidang.social_network.dto.response.LikePostResponse;
import com.hoanghaidang.social_network.entity.*;
import com.hoanghaidang.social_network.exception.CustomException;
import com.hoanghaidang.social_network.mapper.LikeMapper;
import com.hoanghaidang.social_network.service.inter.ILikeCommentService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LikeCommentService implements ILikeCommentService {
    LikeCommentRepository likeCommentRepository;
    CommentRepository commentRepository;
    UserRepository userRepository;
    LikeMapper likeMapper;

    private User getAuthenticatedUser(Authentication authentication) {
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new CustomException("User is not found", HttpStatus.NOT_FOUND));
    }

    private LikeComment checkDuplicateLike(long userId, Long commentId) {
            return likeCommentRepository.findByUserIdAndCommentId(userId, commentId);
    }

    @Override
    public ResponseEntity<ApiResponse<Map<String,Object>>> getLikeCommentList(Authentication authentication, int page, int size) {
        User user = getAuthenticatedUser(authentication);

        Pageable pageable = PageRequest.of(page, size, Sort.by("createAt").descending());

        Page<LikeComment> likeComments = likeCommentRepository.findLikeCommentByUser(user, pageable);

        Page<LikeCommentResponse> likeCommentResponses = likeComments.map(
                likeMapper::toCommentResponse
        );

        Map<String, Object> response = new HashMap<>();
        response.put("likeComments", likeCommentResponses.getContent());
        response.put("currentPage", likeCommentResponses.getNumber());
        response.put("totalItems", likeCommentResponses.getTotalElements());
        response.put("totalPages", likeCommentResponses.getTotalPages());

        ApiResponse<Map<String,Object>> apiResponse = ApiResponse.<Map<String,Object>>builder()
                .data(response)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @Override
    public ResponseEntity<ApiResponse<LikeCommentResponse>> likeComment(Authentication authentication, long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException("The comment is not found", HttpStatus.NOT_FOUND));

        User user = getAuthenticatedUser(authentication);

        if (checkDuplicateLike(user.getId(), commentId) != null) {
            throw new CustomException("Like is duplicate!", HttpStatus.CONFLICT);
        }

        LikeComment like = LikeComment.builder()
                .createAt(LocalDateTime.now())
                .comment(comment)
                .user(user)
                .build();

        comment.setLikeCount(comment.getLikeCount() + 1);
        likeCommentRepository.save(like);
        LikeCommentResponse likeCommentResponse = likeMapper.toCommentResponse(like);

        ApiResponse<LikeCommentResponse> apiResponse = ApiResponse.<LikeCommentResponse>builder()
                .message("Like comment is completed")
                .data(likeCommentResponse)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> unlike(Authentication authentication, long commentId) {
        User user = getAuthenticatedUser(authentication);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException("Comment is not found", HttpStatus.NOT_FOUND));

        LikeComment likeComment = likeCommentRepository.findByUserIdAndCommentId(user.getId(), commentId);

        if(likeComment == null){
            throw new CustomException("Like comment is not found!",HttpStatus.NOT_FOUND);
        }
        comment.setLikeCount(comment.getLikeCount() - 1);
        commentRepository.save(comment);

        likeCommentRepository.delete(likeComment);

        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .message("Delete like is completed")
                .build();
        return ResponseEntity.ok(apiResponse);
    }
}

