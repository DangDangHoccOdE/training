package com.luvina.training_final.SpringBootProject.service;

import com.luvina.training_final.SpringBootProject.entity.Account;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface IUserSecurityService extends UserDetailsService {
    Account findByEmail(String email);
}
