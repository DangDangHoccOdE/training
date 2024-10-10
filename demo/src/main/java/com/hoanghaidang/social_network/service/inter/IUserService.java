package com.hoanghaidang.social_network.service.inter;

import com.hoanghaidang.social_network.dto.LoginDto;
import com.hoanghaidang.social_network.dto.RegistrationDto;
import com.hoanghaidang.social_network.dto.UserDto;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

public interface IUserService {
    ResponseEntity<?> registerUser(RegistrationDto registrationDto) throws Exception;
    ResponseEntity<?> activeUser(String email);
    ResponseEntity<?> login(LoginDto loginDto);
    ResponseEntity<?> validOtp(String otp,String email);
    ResponseEntity<?> updateProfile(String email,UserDto userDto) throws Exception;
    ResponseEntity<?> forgetPassword(String email);
    ResponseEntity<?> changePassword(String email,String token, String newPassword);
    ResponseEntity<?> report(String email) throws IOException;
}
