package com.hoanghaidang.social_network.controller;

import com.hoanghaidang.social_network.service.inter.ILikeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/like")
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@Tag(name = "Like Management", description = "APIs for managing like")
public class LikeController {
    ILikeService iLikeService;

    @PostMapping("/user/{userId}/post/{postId}")
    public ResponseEntity<?> likePost(
            @PathVariable("userId") long userId,
            @PathVariable("postId") long postId){
        return iLikeService.likePost(userId,postId);
    }

    @PostMapping("/user/{userId}/comment/{commentId}")
    public ResponseEntity<?> likeComment(
            @PathVariable("userId") long userId,
            @PathVariable("commentId") long commentId){
        return iLikeService.likeComment(userId,commentId);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteLike(@PathVariable("id") long id){
        return iLikeService.deleteLike(id);
    }
}
