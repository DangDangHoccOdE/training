package com.hoanghaidang.social_network.controller;

import com.hoanghaidang.social_network.dto.PostDto;
import com.hoanghaidang.social_network.service.inter.IPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/post")
public class PostController {
    @Autowired
    private IPostService iPostService;

    @PostMapping("/create")
    public ResponseEntity<?> createPost(@Validated @RequestBody PostDto postDto){
        return iPostService.createPost(postDto);
    }

    @PutMapping("/edit/{postId}")
    public ResponseEntity<?> editPost(@PathVariable long postId,@RequestBody PostDto postDto){
        return iPostService.editPost(postId,postDto);
    }

    @DeleteMapping("/delete/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable long postId){
        return iPostService.deletePost(postId);
    }
}
