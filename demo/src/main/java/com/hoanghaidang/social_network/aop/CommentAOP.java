package com.hoanghaidang.social_network.aop;

import com.hoanghaidang.social_network.dao.CommentRepository;
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
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final SecurityUtils securityUtils;

    @Autowired
    public CommentAOP(CommentRepository commentRepository, UserRepository userRepository, SecurityUtils securityUtils) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.securityUtils = securityUtils;
    }

    @Before(value = "execution(* com.hoanghaidang.social_network.controller.CommentController.createComment(..)) && args(..,commentDto)")
    public void hasAccess(CommentDto commentDto) throws AccessDeniedException {
        User user = userRepository.findUserById(commentDto.getUserId())
                .orElseThrow(()-> new CustomException("User not found", HttpStatus.NOT_FOUND));
        if (securityUtils.hasNotAccessByUserId(user.getId())) {
            throw new AccessDeniedException(ACCESS_DENIED_MESSAGE);
        }
    }
}
