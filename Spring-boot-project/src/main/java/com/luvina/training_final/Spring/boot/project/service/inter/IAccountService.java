package com.luvina.training_final.Spring.boot.project.service.inter;

import com.luvina.training_final.Spring.boot.project.entity.Account;

public interface IAccountService {
    Account findAccountByEmail(String email);
}
