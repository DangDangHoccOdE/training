package com.luvina.training_final.SpringBootProject.service.impl;

import com.luvina.training_final.SpringBootProject.dao.AccountRepository;
import com.luvina.training_final.SpringBootProject.entity.Account;
import com.luvina.training_final.SpringBootProject.exception.CustomException;
import com.luvina.training_final.SpringBootProject.service.inter.IAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class AccountService implements IAccountService {
    @Autowired
    private AccountRepository accountRepository;

    @Override
    public Account findAccountByEmail(String email) {
        return accountRepository.findAccountByEmail(email)
                .orElseThrow(() -> new CustomException("Cannot find account with email", HttpStatus.NOT_FOUND));
    }
}
