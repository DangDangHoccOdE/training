package com.hoanghaidang.social_network.service.inter;

import com.hoanghaidang.social_network.dto.request.LoginDto;
import com.hoanghaidang.social_network.dto.request.RegistrationDto;
import com.hoanghaidang.social_network.dto.request.UserDto;
import com.hoanghaidang.social_network.dto.request.UserRequestDto;
import com.hoanghaidang.social_network.dto.response.ApiResponse;
import com.hoanghaidang.social_network.dto.response.ForgetPasswordResponse;
import com.hoanghaidang.social_network.dto.response.UserResponse;
import com.hoanghaidang.social_network.entity.Notice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.io.IOException;

public interface IUserService {
    ResponseEntity<ApiResponse<Void>> sendEmailActive(UserRequestDto userRequestDto);
    ResponseEntity<ApiResponse<?>> refreshToken(Authentication authentication,String refreshToken);
    ResponseEntity<ApiResponse<Void>> registerUser(RegistrationDto registrationDto) throws Exception;
    ResponseEntity<ApiResponse<Void>> activeUser(String email,String token);
    ResponseEntity<ApiResponse<?>> login(LoginDto loginDto);
    ResponseEntity<ApiResponse<?>> validOtp(String otp,String email);
    ResponseEntity<ApiResponse<UserResponse>> updateProfile(UserDto userDto, Authentication authentication) throws Exception;
    ResponseEntity<ApiResponse<ForgetPasswordResponse>> forgetPassword(String email);
    ResponseEntity<ApiResponse<Void>> changePassword(String email,String token, String newPassword);
    ResponseEntity<?> report(String email) throws IOException;
}
