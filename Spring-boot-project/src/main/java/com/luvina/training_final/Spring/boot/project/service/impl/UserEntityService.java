package com.luvina.training_final.Spring.boot.project.service.impl;

import com.luvina.training_final.Spring.boot.project.dao.UserEntityRepository;
import com.luvina.training_final.Spring.boot.project.dto.AccountDto;
import com.luvina.training_final.Spring.boot.project.entity.Account;
import com.luvina.training_final.Spring.boot.project.service.inter.IUserEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class UserEntityService implements IUserEntityService {
    @Autowired
    private UserEntityRepository userEntityRepository;

    @Autowired
    private AccountService accountService;

    @Override
    public ResponseEntity<?> registerUser(AccountDto accountDto) {
        Account account = accountService.findAccountByEmail(accountDto.getEmail());

        if(account == null){
        }
        return null;
    }
}
