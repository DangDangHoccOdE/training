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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostService implements IPostService {
    PostRepository postRepository;
    UserRepository userRepository;

    private User getAuthenticatedUser(Authentication authentication) {
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new CustomException("User is not found", HttpStatus.NOT_FOUND));
    }

    private void checkPostOwnership(User user, Post post) {
        if (post.getUser() != user) {
            throw new AccessDeniedException("You do not have access");
        }
    }

    private Post getPost(long postId){
        return postRepository.findById(postId)
                .orElseThrow(() -> new CustomException("Post could not be found", HttpStatus.NOT_FOUND));
    }
    @Override
    public ResponseEntity<Notice> createPost(Authentication authentication, PostDto postDto) {
        if (postDto.getContent() == null && postDto.getTitle() == null && postDto.getImage() == null) {
            throw new CustomException("Post is required a content or a title or images", HttpStatus.BAD_REQUEST);
        }

        User user = getAuthenticatedUser(authentication);

        Post post = Post.builder()
                .title(postDto.getTitle())
                .content(postDto.getContent())
                .createAt(LocalDateTime.now())
                .status(postDto.getStatus())
                .image(postDto.getImage())
                .user(user)
                .build();

        postRepository.save(post);
        return ResponseEntity.ok(new Notice("Create post completed!"));
    }

    @Override
    public ResponseEntity<PostDto> editPost(Authentication authentication, long postId, PostDto postDto) {
        Post post = getPost(postId);

        User user = getAuthenticatedUser(authentication);
        checkPostOwnership(user, post);

        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());
        post.setStatus(postDto.getStatus());
        post.setUpdateAt(LocalDateTime.now());
        post.setImage(postDto.getImage());

        postRepository.save(post);
        return ResponseEntity.ok(postDto);
    }

    @Override
    public ResponseEntity<Notice> deletePost(Authentication authentication, long postId) {
        Post post = getPost(postId);

        User user = getAuthenticatedUser(authentication);
        checkPostOwnership(user, post);

        postRepository.delete(post);
        return ResponseEntity.ok(new Notice("Delete post completed"));
    }
}

