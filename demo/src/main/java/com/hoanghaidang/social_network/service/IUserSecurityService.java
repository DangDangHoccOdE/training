package com.hoanghaidang.social_network.service;

import com.hoanghaidang.social_network.entity.Account;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface IUserSecurityService extends UserDetailsService {
    Account findByEmail(String email);
}
