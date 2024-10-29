package com.hoanghaidang.social_network.controller;

import com.hoanghaidang.social_network.dto.request.LikeCommentDto;
import com.hoanghaidang.social_network.dto.request.LikePostDto;
import com.hoanghaidang.social_network.dto.response.ApiResponse;
import com.hoanghaidang.social_network.dto.response.LikeCommentResponse;
import com.hoanghaidang.social_network.dto.response.LikePostResponse;
import com.hoanghaidang.social_network.service.inter.ILikeCommentService;
import com.hoanghaidang.social_network.service.inter.ILikePostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/like")
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@Tag(name = "Like Management", description = "APIs for managing like")
public class LikeController {
    ILikeCommentService iLikeCommentService;
    ILikePostService iLikePostService;

    @Operation(summary = "Like post", description = "Like post")
    @PostMapping("/like_post")
    public ResponseEntity<ApiResponse<LikePostResponse>> likePost(
            @Validated @RequestBody LikePostDto likePostDto,
            Authentication authentication){
        return iLikePostService.likePost(authentication,likePostDto.getPostId());
    }

    @Operation(summary = "Like comment", description = "Like comment")
    @PostMapping("/like_comment")
    public ResponseEntity<ApiResponse<LikeCommentResponse>> likeComment(
            @Validated @RequestBody LikeCommentDto likeCommentDto,
            Authentication authentication){
        return iLikeCommentService.likeComment(authentication,likeCommentDto.getCommentId());
    }

    @Operation(summary = "Unlike comment", description = "Unlike comment")
    @DeleteMapping("/unlikeComment/{commentId}")
    public ResponseEntity<ApiResponse<Void>> unlikeComment(@PathVariable("commentId") long id, Authentication authentication){
        return iLikeCommentService.unlike(authentication,id);
    }

    @Operation(summary = "Unlike post", description = "Unlike post")
    @DeleteMapping("/unlikePost/{postId}")
    public ResponseEntity<ApiResponse<Void>> unlikePost(@PathVariable("postId") long id, Authentication authentication){
        return iLikePostService.unlike(authentication,id);
    }
}
