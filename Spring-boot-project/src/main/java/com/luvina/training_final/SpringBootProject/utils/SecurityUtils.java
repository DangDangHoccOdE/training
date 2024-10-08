package com.luvina.training_final.SpringBootProject.utils;

import com.luvina.training_final.SpringBootProject.dao.AccountRepository;
import com.luvina.training_final.SpringBootProject.entity.Account;
import com.luvina.training_final.SpringBootProject.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {
    private final AccountRepository accountRepository;

    @Autowired
    public SecurityUtils(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public boolean hasNotAccessByUserId(Long userId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return !isAdmin(authentication) && !isUserIdMatch(authentication, userId);
    }

    private boolean isUserIdMatch(Authentication authentication,Long userId){
        Long authenticationUserId = getUserIdFromPrincipal(authentication);
        return authenticationUserId.equals(userId);
    }

    private Long getUserIdFromPrincipal(Authentication authentication){
        Account account = accountRepository.findAccountByEmail(authentication.getName())
                .orElseThrow(()->new CustomException("Account is not found", HttpStatus.NOT_FOUND));

        return account.getUser().getId();
    }

    private boolean isAdmin(Authentication authentication){
        return authentication.getAuthorities().stream()
                .anyMatch(authority->authority.getAuthority().equals("ROLE_ADMIN"));
    }
}
