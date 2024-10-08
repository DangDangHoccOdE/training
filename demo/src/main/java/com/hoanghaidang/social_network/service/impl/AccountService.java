package com.hoanghaidang.social_network.service.impl;

import com.hoanghaidang.social_network.dao.AccountRepository;
import com.hoanghaidang.social_network.entity.Account;
import com.hoanghaidang.social_network.exception.CustomException;
import com.hoanghaidang.social_network.service.inter.IAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class AccountService {
    @Autowired
    private AccountRepository accountRepository;

    public Account findAccountByEmail(String email) {
        return accountRepository.findAccountByEmail(email)
                .orElseThrow(() -> new CustomException("Cannot find account with email", HttpStatus.NOT_FOUND));
    }
}
