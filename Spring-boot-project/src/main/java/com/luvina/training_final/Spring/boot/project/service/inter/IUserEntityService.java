package com.luvina.training_final.Spring.boot.project.service.inter;

import com.luvina.training_final.Spring.boot.project.dto.AccountDto;
import org.springframework.http.ResponseEntity;

public interface IUserEntityService {
    ResponseEntity<?> registerUser(AccountDto accountDto);
}
