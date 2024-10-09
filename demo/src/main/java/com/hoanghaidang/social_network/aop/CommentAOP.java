package com.hoanghaidang.social_network.aop;

import com.hoanghaidang.social_network.dao.UserRepository;
import com.hoanghaidang.social_network.dto.CommentDto;
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
public class CommentAOP {
    private static final String ACCESS_DENIED_MESSAGE = "You do not have access!";
    private final UserRepository userRepository;
    private final SecurityUtils securityUtils;

    @Autowired
    public CommentAOP(UserRepository userRepository, SecurityUtils securityUtils) {
        this.userRepository = userRepository;
        this.securityUtils = securityUtils;
    }

    @Before(value = "execution(* com.hoanghaidang.social_network.controller.CommentController.createComment(..)) && args(..,userId,postId,commentDto)", argNames = "userId,postId,commentDto")
    public void hasAccess(long userId, long postId,CommentDto commentDto) throws AccessDeniedException {
        User user = userRepository.findUserById(userId)
                .orElseThrow(()-> new CustomException("User not found", HttpStatus.NOT_FOUND));
        if (securityUtils.hasNotAccessByUserId(user.getId())) {
            throw new AccessDeniedException(ACCESS_DENIED_MESSAGE);
        }
    }
}
