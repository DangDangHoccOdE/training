package com.luvina.training_final.Spring.boot.project.service.inter;

import com.luvina.training_final.Spring.boot.project.dto.AccountDto;
import com.luvina.training_final.Spring.boot.project.dto.RegistrationDto;
import org.springframework.http.ResponseEntity;

public interface IUserEntityService {
    ResponseEntity<?> registerUser(RegistrationDto registrationDto) throws Exception;
    ResponseEntity<?> activeAccount(String email);
    ResponseEntity<?> login(AccountDto accountDto);
    ResponseEntity<?> validOtp(String otp,AccountDto accountDto);
}
