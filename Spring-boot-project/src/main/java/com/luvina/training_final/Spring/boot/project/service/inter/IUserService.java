package com.luvina.training_final.Spring.boot.project.service.inter;

import com.luvina.training_final.Spring.boot.project.dto.AccountDto;
import com.luvina.training_final.Spring.boot.project.dto.RegistrationDto;
import com.luvina.training_final.Spring.boot.project.dto.UserDto;
import org.springframework.http.ResponseEntity;

public interface IUserService {
    ResponseEntity<?> registerUser(RegistrationDto registrationDto) throws Exception;
    ResponseEntity<?> activeAccount(String email);
    ResponseEntity<?> login(AccountDto accountDto);
    ResponseEntity<?> validOtp(String otp,AccountDto accountDto);
    ResponseEntity<?> updateProfile(long id,UserDto userDto) throws Exception;
    ResponseEntity<?> forgetPassword(long id);
    ResponseEntity<?> changePassword(long id,String token, String newPassword);
}
