package com.hoanghaidang.social_network.aop;

import com.hoanghaidang.social_network.dao.CommentRepository;
import com.hoanghaidang.social_network.dao.UserRepository;
import com.hoanghaidang.social_network.dto.CommentDto;
import com.hoanghaidang.social_network.entity.Comment;
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
public class CommentAOP {
    static final String ACCESS_DENIED_MESSAGE = "You do not have access!";
    UserRepository userRepository;
    SecurityUtils securityUtils;
    CommentRepository commentRepository;

    @Autowired
    public CommentAOP(UserRepository userRepository, SecurityUtils securityUtils, CommentRepository commentRepository) {
        this.userRepository = userRepository;
        this.securityUtils = securityUtils;
        this.commentRepository = commentRepository;

    }

    @Before(value = "execution(* com.hoanghaidang.social_network.controller.CommentController.createComment(..)) && args(..,commentDto)")
    public void hasAccess(CommentDto commentDto) throws AccessDeniedException {
        User user = userRepository.findUserById(commentDto.getUserId())
                .orElseThrow(()-> new CustomException("User not found", HttpStatus.NOT_FOUND));

        if (securityUtils.hasNotAccessByUserId(user.getId())) {
            throw new AccessDeniedException(ACCESS_DENIED_MESSAGE);
        }
    }

    @Before(value = "execution(* com.hoanghaidang.social_network.controller.CommentController.editComment(..)) && args(..,commentId,commentDto)", argNames = "commentId,commentDto")
    public void hasAccess(long commentId,CommentDto commentDto) throws AccessDeniedException {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(()->new CustomException("Comment not found", HttpStatus.NOT_FOUND));

        User user = comment.getUser();
        if (securityUtils.hasNotAccessByUserId(user.getId())) {
            throw new AccessDeniedException(ACCESS_DENIED_MESSAGE);
        }
    }

    @Before(value = "execution(* com.hoanghaidang.social_network.controller.CommentController.deleteComment(..)) && args(..,commentId)")
    public void hasAccess(long commentId) throws AccessDeniedException {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(()->new CustomException("Comment not found", HttpStatus.NOT_FOUND));

        User user = comment.getUser();
        if (securityUtils.hasNotAccessByUserId(user.getId())) {
            throw new AccessDeniedException(ACCESS_DENIED_MESSAGE);
        }
    }
}
