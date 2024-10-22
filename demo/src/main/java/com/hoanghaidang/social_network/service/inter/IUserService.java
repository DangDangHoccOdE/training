package com.hoanghaidang.social_network.service.inter;

import com.hoanghaidang.social_network.dto.request.LoginDto;
import com.hoanghaidang.social_network.dto.request.RegistrationDto;
import com.hoanghaidang.social_network.dto.request.UserDto;
import com.hoanghaidang.social_network.dto.response.ApiResponse;
import com.hoanghaidang.social_network.dto.response.LoginResponse;
import com.hoanghaidang.social_network.dto.response.UserResponse;
import com.hoanghaidang.social_network.entity.Notice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.io.IOException;

public interface IUserService {
    ResponseEntity<?> refreshToken(Authentication authentication,String refreshToken);
    ResponseEntity<Notice> registerUser(RegistrationDto registrationDto) throws Exception;
    ResponseEntity<Notice> activeUser(String email);
    ResponseEntity<?> login(LoginDto loginDto);
    ResponseEntity<?> validOtp(String otp,String email);
    ResponseEntity<UserResponse> updateProfile(UserDto userDto, Authentication authentication) throws Exception;
    ResponseEntity<ApiResponse> forgetPassword(String email);
    ResponseEntity<Notice> changePassword(String email,String token, String newPassword);
    ResponseEntity<?> report(String email) throws IOException;
}
