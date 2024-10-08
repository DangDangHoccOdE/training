package com.hoanghaidang.social_network.utils;

import com.hoanghaidang.social_network.dao.UserRepository;
import com.hoanghaidang.social_network.entity.User;
import com.hoanghaidang.social_network.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {
    private final UserRepository userRepository;

    @Autowired
    public SecurityUtils(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean hasNotAccessByUserId(Long userId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return !isUserIdMatch(authentication, userId);
    }

    private boolean isUserIdMatch(Authentication authentication,Long userId){
        Long authenticationUserId = getUserIdFromPrincipal(authentication);
        return authenticationUserId.equals(userId);
    }

    private Long getUserIdFromPrincipal(Authentication authentication){
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(()->new CustomException("Account is not found", HttpStatus.NOT_FOUND));

        return user.getId();
    }
}
