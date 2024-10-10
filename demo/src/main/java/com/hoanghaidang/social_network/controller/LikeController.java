package com.hoanghaidang.social_network.controller;

import com.hoanghaidang.social_network.dto.LikeCommentDto;
import com.hoanghaidang.social_network.dto.LikePostDto;
import com.hoanghaidang.social_network.service.inter.ILikeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/like")
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@Tag(name = "Like Management", description = "APIs for managing like")
public class LikeController {
    ILikeService iLikeService;

//    @PostMapping("/user/{userId}/post/{postId}")
//    public ResponseEntity<?> likePost(
//            @PathVariable("userId") long userId,
//            @PathVariable("postId") long postId){
//        return iLikeService.likePost(userId,postId);
//    }

    @PostMapping("/like_post")
    public ResponseEntity<?> likePost(
            @Validated @RequestBody LikePostDto likePostDto){
        return iLikeService.likePost(likePostDto.getUserId(),likePostDto.getPostId());
    }

    @PostMapping("/like_comment")
    public ResponseEntity<?> likeComment(
            @Validated @RequestBody LikeCommentDto likeCommentDto){
        return iLikeService.likeComment(likeCommentDto.getUserId(),likeCommentDto.getPostId());
    }

    @DeleteMapping("/unlike/{id}")
    public ResponseEntity<?> unlike(@PathVariable("id") long id){
        return iLikeService.unlike(id);
    }
}
