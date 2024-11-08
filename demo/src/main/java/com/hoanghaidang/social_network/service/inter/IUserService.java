package com.hoanghaidang.social_network.service.inter;

import com.hoanghaidang.social_network.dto.request.LoginDto;
import com.hoanghaidang.social_network.dto.request.RegistrationDto;
import com.hoanghaidang.social_network.dto.request.UserDto;
import com.hoanghaidang.social_network.dto.request.UserRequestDto;
import com.hoanghaidang.social_network.dto.response.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.io.IOException;
import java.util.List;

public interface IUserService {
    ResponseEntity<ApiResponse<UserResponse>> getUserById(Long userId);
    ResponseEntity<ApiResponse<List<UserResponse>>> getAllUser(int size, int page);
    ResponseEntity<ApiResponse<Void>> sendEmailActive(UserRequestDto userRequestDto);
    ResponseEntity<ApiResponse<JwtResponse>> refreshToken(Authentication authentication,String refreshToken);
    ResponseEntity<ApiResponse<Void>> registerUser(RegistrationDto registrationDto) throws Exception;
    ResponseEntity<ApiResponse<Void>> activeUser(String email,String token);
    ResponseEntity<ApiResponse<LoginResponse>> login(LoginDto loginDto);
    ResponseEntity<ApiResponse<JwtResponse>> validOtp(String otp, String email);
    ResponseEntity<ApiResponse<UserResponse>> updateProfile(UserDto userDto, Authentication authentication) throws Exception;
    ResponseEntity<ApiResponse<ForgetPasswordResponse>> forgetPassword(String email);
    ResponseEntity<ApiResponse<Void>> changePassword(String email,String token, String newPassword);
    ResponseEntity<?> report(String email) throws IOException;
}
