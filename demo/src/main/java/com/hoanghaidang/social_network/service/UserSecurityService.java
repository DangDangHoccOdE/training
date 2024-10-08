package com.hoanghaidang.social_network.service;

import com.hoanghaidang.social_network.dao.AccountRepository;
import com.hoanghaidang.social_network.entity.Account;
import com.hoanghaidang.social_network.entity.Role;
import com.hoanghaidang.social_network.entity.User;
import com.hoanghaidang.social_network.exception.CustomException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserSecurityService implements IUserSecurityService {
    AccountRepository accountRepository;

    @Override
    public Account findByEmail(String email) {
        return accountRepository.findAccountByEmail(email)
                .orElseThrow(() -> new CustomException("Cannot find account with email", HttpStatus.NOT_FOUND));
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Account account = findByEmail(email);

        if(!account.isActive()){
            throw new CustomException("Account has not been activated",HttpStatus.BAD_REQUEST);
        }
        User user = account.getUser();
        return new org.springframework.security.core.userdetails.User(account.getEmail(),account.getPassword(),userAuthority(user.getRoles()));
    }

    private Collection<? extends GrantedAuthority> userAuthority(Collection<Role> roles) {
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getRoleName())).collect(Collectors.toList());
    }
}
