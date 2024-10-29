package com.hoanghaidang.social_network.controller;

import com.hoanghaidang.social_network.dto.request.PostDto;
import com.hoanghaidang.social_network.dto.response.ApiResponse;
import com.hoanghaidang.social_network.dto.response.PostResponse;
import com.hoanghaidang.social_network.entity.Notice;
import com.hoanghaidang.social_network.service.inter.IPostService;
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
@RequestMapping("/api/post")
@Tag(name = "Post Management", description = "APIs for managing post")
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class PostController {
    private IPostService iPostService;

    @Operation(summary = "Create Post", description = "Create Post")
    @PostMapping("/create_post")
    public ResponseEntity<ApiResponse<PostResponse>> createPost(Authentication authentication, @Validated @RequestBody PostDto postDto){
        return iPostService.createPost(authentication,postDto);
    }

    @Operation(summary = "Edit Post", description = "Edit Post")
    @PutMapping("/edit/{postId}")
    public ResponseEntity<ApiResponse<PostResponse>> editPost(Authentication authentication
            , @PathVariable long postId
            ,@Validated @RequestBody PostDto postDto){
        return iPostService.editPost(authentication,postId,postDto);
    }

    @Operation(summary = "Delete Post", description = "Delete Post")
    @DeleteMapping("/delete/{postId}")
    public ResponseEntity<ApiResponse<Void>> deletePost(Authentication authentication,@PathVariable long postId){
        return iPostService.deletePost(authentication,postId);
    }
}
