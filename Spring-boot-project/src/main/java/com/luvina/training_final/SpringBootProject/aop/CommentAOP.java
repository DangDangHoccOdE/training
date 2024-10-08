package com.luvina.training_final.SpringBootProject.aop;

import com.luvina.training_final.SpringBootProject.dao.CommentRepository;
import com.luvina.training_final.SpringBootProject.dao.UserRepository;
import com.luvina.training_final.SpringBootProject.dto.CommentDto;
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

    @Before(value = "execution(* com.luvina.training_final.SpringBootProject.controller.CommentController.createComment(..)) && args(..,commentDto)")
    public void hasAccess(CommentDto commentDto) throws AccessDeniedException {
        User user = userRepository.findUserById(commentDto.getUserId())
                .orElseThrow(()-> new CustomException("User not found", HttpStatus.NOT_FOUND));
        if (securityUtils.hasNotAccessByUserId(user.getId())) {
            throw new AccessDeniedException(ACCESS_DENIED_MESSAGE);
        }
    }
}
