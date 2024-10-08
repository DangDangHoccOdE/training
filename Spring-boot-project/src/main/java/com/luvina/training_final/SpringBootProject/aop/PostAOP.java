package com.luvina.training_final.SpringBootProject.aop;

import com.luvina.training_final.SpringBootProject.dao.PostRepository;
import com.luvina.training_final.SpringBootProject.dao.UserRepository;
import com.luvina.training_final.SpringBootProject.dto.PostDto;
import com.luvina.training_final.SpringBootProject.entity.Post;
import com.luvina.training_final.SpringBootProject.entity.User;
import com.luvina.training_final.SpringBootProject.exception.CustomException;
import com.luvina.training_final.SpringBootProject.utils.SecurityUtils;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PostAOP {
    private static final String ACCESS_DENIED_MESSAGE = "You do not have access!";
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final SecurityUtils securityUtils;

    @Autowired
    public PostAOP(PostRepository postRepository, UserRepository userRepository, SecurityUtils securityUtils) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.securityUtils = securityUtils;
    }

    @Before(value = "execution(* com.luvina.training_final.SpringBootProject.controller.PostController.createPost(..)) && args(..,postDto)")
    public void hasAccessSendFriendship(PostDto postDto) throws AccessDeniedException {
        User user = userRepository.findUserById(postDto.getUserId())
                .orElseThrow(()-> new CustomException("User not found", HttpStatus.NOT_FOUND));
        if (securityUtils.hasNotAccessByUserId(user.getId())) {
            throw new AccessDeniedException(ACCESS_DENIED_MESSAGE);
        }
    }

    @Before(value = "execution(* com.luvina.training_final.SpringBootProject.controller.PostController.editPost(..)) && args(..,postId,postDto)", argNames = "postId,postDto")
    public void hasAccessSendFriendship(long postId,PostDto postDto) throws AccessDeniedException {
        User user = userRepository.findUserById(postDto.getUserId())
                .orElseThrow(()-> new CustomException("User not found", HttpStatus.NOT_FOUND));
        if (securityUtils.hasNotAccessByUserId(user.getId())) {
            throw new AccessDeniedException(ACCESS_DENIED_MESSAGE);
        }
    }

    @Before(value = "execution(* com.luvina.training_final.SpringBootProject.controller.PostController.deletePost(..)) && args(..,postId)")
    public void hasAccessSendFriendship(long postId) throws AccessDeniedException {
        Post post = postRepository.findById(postId)
                .orElseThrow(()-> new CustomException("Post not found", HttpStatus.NOT_FOUND));
        if (securityUtils.hasNotAccessByUserId(post.getUser().getId())) {
            throw new AccessDeniedException(ACCESS_DENIED_MESSAGE);
        }
    }
}
