package com.luvina.training_final.Spring.boot.project.service.impl;

import com.luvina.training_final.Spring.boot.project.dao.PostRepository;
import com.luvina.training_final.Spring.boot.project.dao.UserRepository;
import com.luvina.training_final.Spring.boot.project.dto.PostDto;
import com.luvina.training_final.Spring.boot.project.entity.Notice;
import com.luvina.training_final.Spring.boot.project.entity.Post;
import com.luvina.training_final.Spring.boot.project.entity.User;
import com.luvina.training_final.Spring.boot.project.exception.CustomException;
import com.luvina.training_final.Spring.boot.project.service.inter.IPostService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
        User user = userRepository.findUserById(postDto.getUserId())
                .orElseThrow(()->new CustomException("User not found", HttpStatus.NOT_FOUND));

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
        Post post = postRepository.findById(postId)
                .orElseThrow(()->new CustomException("Post can not found",HttpStatus.NOT_FOUND));

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
        Post post = postRepository.findById(postId)
                .orElseThrow(()->new CustomException("Post can not found",HttpStatus.NOT_FOUND));

        postRepository.delete(post);
        return ResponseEntity.ok(new Notice("Delete post completed"));
    }
}
