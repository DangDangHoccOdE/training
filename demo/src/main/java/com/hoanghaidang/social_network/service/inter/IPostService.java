package com.hoanghaidang.social_network.service.inter;

import com.hoanghaidang.social_network.dto.PostDto;
import org.springframework.http.ResponseEntity;

public interface IPostService {
    ResponseEntity<?> createPost(PostDto postDto);
    ResponseEntity<?> editPost(long postId,PostDto postDto);
    ResponseEntity<?> deletePost(long postId);
}
