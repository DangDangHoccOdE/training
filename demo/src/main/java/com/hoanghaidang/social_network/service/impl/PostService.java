package com.hoanghaidang.social_network.service.impl;

import com.hoanghaidang.social_network.dao.PostRepository;
import com.hoanghaidang.social_network.dao.UserRepository;
import com.hoanghaidang.social_network.dto.PostDto;
import com.hoanghaidang.social_network.entity.Notice;
import com.hoanghaidang.social_network.entity.Post;
import com.hoanghaidang.social_network.entity.User;
import com.hoanghaidang.social_network.service.inter.IPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PostService implements IPostService {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;
    @Override
    public ResponseEntity<?> createPost(PostDto postDto) {
        User user = userRepository.findUserById(postDto.getUserId()).get();
//                .orElseThrow(()->new CustomException("User not found", HttpStatus.NOT_FOUND));

        Post post = Post.builder()
                .title(postDto.getTitle())
                .content(postDto.getContent())
                .createAt(LocalDateTime.now())
                .status(postDto.getStatus())
                .likeCount(0)
                .commentCount(0)
                .image(postDto.getImage())
                .user(user)
                .build();

        postRepository.save(post);
        return ResponseEntity.ok(new Notice("Create post completed!"));
    }

    @Override
    public ResponseEntity<?> editPost(long postId,PostDto postDto) {
        Post post = postRepository.findById(postId).get();
//                .orElseThrow(()->new CustomException("Post can not found",HttpStatus.NOT_FOUND));

        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());
        post.setStatus(postDto.getStatus());
        post.setUpdateAt(LocalDateTime.now());
        post.setImage(postDto.getImage());
        postRepository.save(post);
        return ResponseEntity.ok(new Notice("Edit post completed"));
    }

    @Override
    public ResponseEntity<?> deletePost(long postId) {
        Post post = postRepository.findById(postId).get();
//                .orElseThrow(()->new CustomException("Post can not found",HttpStatus.NOT_FOUND));

        postRepository.delete(post);
        return ResponseEntity.ok(new Notice("Delete post completed"));
    }
}
