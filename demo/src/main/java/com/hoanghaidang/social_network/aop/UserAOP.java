package com.hoanghaidang.social_network.aop;

import com.hoanghaidang.social_network.dao.UserRepository;
import com.hoanghaidang.social_network.dto.UserDto;
import com.hoanghaidang.social_network.dto.UserRequestDto;
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
public class UserAOP {
    private static final String ACCESS_DENIED_MESSAGE = "You do not have access!";
    private final UserRepository userRepository;
    private final SecurityUtils securityUtils;

    @Autowired
    public UserAOP(UserRepository userRepository, SecurityUtils securityUtils) {
        this.userRepository = userRepository;
        this.securityUtils = securityUtils;
    }

    @Before(value = "execution(* com.hoanghaidang.social_network.controller.UserController.updateProfile(..)) && args(..,email,userDto)", argNames = "email,userDto")
    public void hasAccessUpdateProfile(String email, UserDto userDto) throws AccessDeniedException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new CustomException("The user could not be found", HttpStatus.NOT_FOUND));
        if (securityUtils.hasNotAccessByUserId(user.getId())) {
            throw new AccessDeniedException(ACCESS_DENIED_MESSAGE);
        }
    }

    @Before(value = "execution(* com.hoanghaidang.social_network.controller.UserController.report(..)) && args(..,email)")
    public void hasAccessUpdateProfile(String email) throws AccessDeniedException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new CustomException("The user could not be found", HttpStatus.NOT_FOUND));
        if (securityUtils.hasNotAccessByUserId(user.getId())) {
            throw new AccessDeniedException(ACCESS_DENIED_MESSAGE);
        }
    }
}
