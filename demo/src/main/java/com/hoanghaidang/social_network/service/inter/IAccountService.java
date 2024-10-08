package com.hoanghaidang.social_network.service.inter;

import com.hoanghaidang.social_network.entity.Account;

public interface IAccountService {
    Account findAccountByEmail(String email);
}
