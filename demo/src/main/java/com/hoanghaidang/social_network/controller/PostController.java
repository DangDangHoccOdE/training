package com.hoanghaidang.social_network.controller;

import com.hoanghaidang.social_network.dto.PostDto;
import com.hoanghaidang.social_network.service.inter.IPostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/post")
@Tag(name = "Post Management", description = "APIs for managing post")
public class PostController {
    @Autowired
    private IPostService iPostService;

    @Operation(summary = "Create Post", description = "Create Post")
    @PostMapping("/create_post")
    public ResponseEntity<?> createPost(Authentication authentication, @Validated @RequestBody PostDto postDto){
        return iPostService.createPost(authentication,postDto);
    }

    @Operation(summary = "Edit Post", description = "Edit Post")
    @PutMapping("/edit/{postId}")
    public ResponseEntity<?> editPost(Authentication authentication
            ,@PathVariable long postId
            ,@RequestBody PostDto postDto){
        return iPostService.editPost(authentication,postId,postDto);
    }

    @Operation(summary = "Delete Post", description = "Delete Post")
    @DeleteMapping("/delete/{postId}")
    public ResponseEntity<?> deletePost(Authentication authentication,@PathVariable long postId){
        return iPostService.deletePost(authentication,postId);
    }
}
