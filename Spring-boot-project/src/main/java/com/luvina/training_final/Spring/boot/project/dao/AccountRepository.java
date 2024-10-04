package com.luvina.training_final.Spring.boot.project.dao;

import com.luvina.training_final.Spring.boot.project.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "account")
public interface AccountRepository extends JpaRepository<Account, Long> {
    Account findAccountsByEmail(String email);
}
