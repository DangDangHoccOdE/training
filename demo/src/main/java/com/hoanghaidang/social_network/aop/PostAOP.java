package com.hoanghaidang.social_network.aop;

import com.hoanghaidang.social_network.dao.PostRepository;
import com.hoanghaidang.social_network.dao.UserRepository;
import com.hoanghaidang.social_network.dto.PostDto;
import com.hoanghaidang.social_network.entity.Post;
import com.hoanghaidang.social_network.entity.User;
import com.hoanghaidang.social_network.exception.CustomException;
import com.hoanghaidang.social_network.utils.SecurityUtils;
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

    @Before(value = "execution(* com.hoanghaidang.social_network.controller.PostController.createPost(..)) && args(..,userId,postDto)", argNames = "userId,postDto")
    public void hasAccessCreatePost(long userId,PostDto postDto) throws AccessDeniedException {
        User user = userRepository.findUserById(userId)
                .orElseThrow(()-> new CustomException("The user could not be found", HttpStatus.NOT_FOUND));
        if (securityUtils.hasNotAccessByUserId(user.getId())) {
            throw new AccessDeniedException(ACCESS_DENIED_MESSAGE);
        }
    }

    @Before(value = "execution(* com.hoanghaidang.social_network.controller.PostController.editPost(..)) && args(..,postId,postDto)", argNames = "postId,postDto")
    public void hasAccessEditPost(long postId,PostDto postDto) throws AccessDeniedException {
        Post post = postRepository.findById(postId)
                .orElseThrow(()-> new CustomException("Post could not be found", HttpStatus.NOT_FOUND));

        User user = post.getUser();
        if (securityUtils.hasNotAccessByUserId(user.getId())) {
            throw new AccessDeniedException(ACCESS_DENIED_MESSAGE);
        }
    }

    @Before(value = "execution(* com.hoanghaidang.social_network.controller.PostController.deletePost(..)) && args(..,postId)")
    public void hasAccessDelete(long postId) throws AccessDeniedException {
        Post post = postRepository.findById(postId)
                .orElseThrow(()-> new CustomException("The user could not be found", HttpStatus.NOT_FOUND));
        if (securityUtils.hasNotAccessByUserId(post.getUser().getId())) {
            throw new AccessDeniedException(ACCESS_DENIED_MESSAGE);
        }
    }
}
