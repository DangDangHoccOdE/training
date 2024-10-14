package com.hoanghaidang.social_network.service.inter;

import com.hoanghaidang.social_network.dto.LoginDto;
import com.hoanghaidang.social_network.dto.RegistrationDto;
import com.hoanghaidang.social_network.dto.UserDto;
import com.hoanghaidang.social_network.entity.ApiResponse;
import com.hoanghaidang.social_network.entity.Notice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.io.IOException;

public interface IUserService {
    ResponseEntity<?> refreshToken(Authentication authentication,String refreshToken);
    ResponseEntity<Notice> registerUser(RegistrationDto registrationDto) throws Exception;
    ResponseEntity<Notice> activeUser(String email);
    ResponseEntity<Notice> login(LoginDto loginDto);
    ResponseEntity<?> validOtp(String otp,String email);
    ResponseEntity<UserDto> updateProfile(String email, UserDto userDto, Authentication authentication) throws Exception;
    ResponseEntity<ApiResponse> forgetPassword(String email);
    ResponseEntity<Notice> changePassword(String email,String token, String newPassword);
    ResponseEntity<?> report(String email) throws IOException;
}
