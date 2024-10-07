package com.luvina.training_final.Spring.boot.project.service.inter;

import com.luvina.training_final.Spring.boot.project.dto.PostDto;
import org.springframework.http.ResponseEntity;

public interface IPostService {
    ResponseEntity<?> createPost(PostDto postDto);
    ResponseEntity<?> editPost(long postId,PostDto postDto);
    ResponseEntity<?> deletePost(long postId);
}
