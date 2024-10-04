package com.luvina.training_final.Spring.boot.project.service;

import com.luvina.training_final.Spring.boot.project.entity.Account;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface IUserSecurityService extends UserDetailsService {
    Account findByEmail(String email);
}
