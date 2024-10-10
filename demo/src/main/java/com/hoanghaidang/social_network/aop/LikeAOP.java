package com.hoanghaidang.social_network.aop;

import com.hoanghaidang.social_network.dao.LikeRepository;
import com.hoanghaidang.social_network.dao.UserRepository;
import com.hoanghaidang.social_network.dto.LikeCommentDto;
import com.hoanghaidang.social_network.dto.LikePostDto;
import com.hoanghaidang.social_network.entity.Like;
import com.hoanghaidang.social_network.entity.User;
import com.hoanghaidang.social_network.exception.CustomException;
import com.hoanghaidang.social_network.utils.SecurityUtils;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Aspect
@Component
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class LikeAOP {
    static final String ACCESS_DENIED_MESSAGE = "You do not have access!";
    UserRepository userRepository;
    SecurityUtils securityUtils;
    private final LikeRepository likeRepository;

    @Autowired
    public LikeAOP(UserRepository userRepository, SecurityUtils securityUtils,LikeRepository likeRepository) {
        this.userRepository = userRepository;
        this.securityUtils = securityUtils;
        this.likeRepository = likeRepository;
    }

    @Before(value = "execution(* com.hoanghaidang.social_network.controller.LikeController.likePost(..)) && args(..,likePostDto)")
    public void hasAccessLikePost(LikePostDto likePostDto) throws AccessDeniedException {
        User user = userRepository.findUserById(likePostDto.getUserId())
                .orElseThrow(()-> new CustomException("User not found", HttpStatus.NOT_FOUND));

        if (securityUtils.hasNotAccessByUserId(user.getId())) {
            throw new AccessDeniedException(ACCESS_DENIED_MESSAGE);
        }
    }

    @Before(value = "execution(* com.hoanghaidang.social_network.controller.LikeController.likeComment(..)) && args(..,likeCommentDto)")
    public void hasAccessLikeComment(LikeCommentDto likeCommentDto) throws AccessDeniedException {
        User user = userRepository.findUserById(likeCommentDto.getUserId())
                .orElseThrow(()-> new CustomException("User not found", HttpStatus.NOT_FOUND));

        if (securityUtils.hasNotAccessByUserId(user.getId())) {
            throw new AccessDeniedException(ACCESS_DENIED_MESSAGE);
        }
    }

    @Before(value = "execution(* com.hoanghaidang.social_network.controller.LikeController.unlike(..)) && args(..,likeId)")
    public void hasAccess(long likeId) throws AccessDeniedException {
        Like like = likeRepository.findById(likeId)
                .orElseThrow(()->new CustomException("Like not found", HttpStatus.NOT_FOUND));

        User user = like.getUser();
        if (securityUtils.hasNotAccessByUserId(user.getId())) {
            throw new AccessDeniedException(ACCESS_DENIED_MESSAGE);
        }
    }
}
