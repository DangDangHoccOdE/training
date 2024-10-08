package com.hoanghaidang.social_network.service.inter;

import com.hoanghaidang.social_network.dto.LoginDto;
import com.hoanghaidang.social_network.dto.RegistrationDto;
import com.hoanghaidang.social_network.dto.UserDto;
import org.springframework.http.ResponseEntity;

public interface IUserService {
    ResponseEntity<?> registerUser(RegistrationDto registrationDto) throws Exception;
    ResponseEntity<?> activeUser(String email);
    ResponseEntity<?> login(LoginDto loginDto);
    ResponseEntity<?> validOtp(String otp,LoginDto loginDto);
    ResponseEntity<?> updateProfile(long id,UserDto userDto) throws Exception;
    ResponseEntity<?> forgetPassword(long id);
    ResponseEntity<?> changePassword(long id,String token, String newPassword);
}
