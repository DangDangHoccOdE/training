package com.hoanghaidang.social_network.controller;

import com.hoanghaidang.social_network.dto.request.LikeCommentDto;
import com.hoanghaidang.social_network.dto.request.LikePostDto;
import com.hoanghaidang.social_network.entity.Notice;
import com.hoanghaidang.social_network.service.inter.ILikeService;
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
    ILikeService iLikeService;

    @Operation(summary = "Like post", description = "Like post")
    @PostMapping("/like_post")
    public ResponseEntity<Notice> likePost(
            @Validated @RequestBody LikePostDto likePostDto,
            Authentication authentication){
        return iLikeService.likePost(authentication,likePostDto.getPostId());
    }

    @Operation(summary = "Like comment", description = "Like comment")
    @PostMapping("/like_comment")
    public ResponseEntity<Notice> likeComment(
            @Validated @RequestBody LikeCommentDto likeCommentDto,
            Authentication authentication){
        return iLikeService.likeComment(authentication,likeCommentDto.getCommentId());
    }

    @Operation(summary = "Unlike", description = "Unlike")
    @DeleteMapping("/unlike/{id}")
    public ResponseEntity<Notice> unlike(@PathVariable("id") long id, Authentication authentication){
        return iLikeService.unlike(authentication,id);
    }
}
