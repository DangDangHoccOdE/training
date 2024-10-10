package com.hoanghaidang.social_network.service.impl;

import com.hoanghaidang.social_network.dao.PostRepository;
import com.hoanghaidang.social_network.dao.UserRepository;
import com.hoanghaidang.social_network.dto.PostDto;
import com.hoanghaidang.social_network.entity.Notice;
import com.hoanghaidang.social_network.entity.Post;
import com.hoanghaidang.social_network.entity.User;
import com.hoanghaidang.social_network.exception.CustomException;
import com.hoanghaidang.social_network.service.inter.IPostService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class PostService implements IPostService {
    PostRepository postRepository;
    UserRepository userRepository;
    @Override
    public ResponseEntity<?> createPost(PostDto postDto) {
        User user = userRepository.findUserById(postDto.getUserId()).get();

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

        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());
        post.setStatus(postDto.getStatus());
        post.setUpdateAt(LocalDateTime.now());
        post.setImage(postDto.getImage());
        postRepository.save(post);
        return ResponseEntity.ok(postDto);
    }

    @Override
    public ResponseEntity<?> deletePost(long postId) {
        Post post = postRepository.findById(postId).get();

        postRepository.delete(post);
        return ResponseEntity.ok(new Notice("Delete post completed"));
    }
}
