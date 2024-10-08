package com.luvina.training_final.SpringBootProject.service.inter;

import com.luvina.training_final.SpringBootProject.entity.Account;

public interface IAccountService {
    Account findAccountByEmail(String email);
}
