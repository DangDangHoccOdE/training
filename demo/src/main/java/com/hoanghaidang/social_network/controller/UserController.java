package com.hoanghaidang.social_network.controller;

import com.hoanghaidang.social_network.dto.request.*;
import com.hoanghaidang.social_network.dto.response.ApiResponse;
import com.hoanghaidang.social_network.dto.response.LoginResponse;
import com.hoanghaidang.social_network.dto.response.UserResponse;
import com.hoanghaidang.social_network.entity.Notice;
import com.hoanghaidang.social_network.service.impl.UserService;
import com.hoanghaidang.social_network.service.inter.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/user")
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@Tag(name = "User Management", description = "APIs for managing user accounts and authentication")
public class UserController {
    IUserService iUserService;
    UserService userService;
    @Operation(summary = "Refresh Token",description = "Refresh Token")
    @PostMapping("/refreshToken")
    public ResponseEntity<?> refreshToken(Authentication authentication,@RequestHeader String refreshToken){
        return iUserService.refreshToken(authentication,refreshToken);
    }

    @Operation(summary = "Report User",description = "Report User during a week")
    @GetMapping("/report")
    public ResponseEntity<?> report(Authentication authentication) throws IOException {
        return iUserService.report(authentication.getName());
    }

    @Operation(summary = "Forget Password", description = "Forget Password")
    @PostMapping("/forget_password")
    public ResponseEntity<ApiResponse> forgetPassword(@Validated @RequestBody UserRequestDto userRequestDto){
        return  userService.forgetPassword(userRequestDto.getEmail());

    }

    @Operation(summary = "Change Password", description = "Change Password")
    @PutMapping("/change_password")
    public ResponseEntity<Notice> changePassword(@Validated @RequestBody RequestForgetPasswordDto requestForgetPasswordDto){
        return iUserService.changePassword(requestForgetPasswordDto.getEmail(),
                requestForgetPasswordDto.getToken(),
                requestForgetPasswordDto.getNewPassword());
    }

    @Operation(summary = "Update Profile", description = "Update Profile for User")
    @PutMapping("/update_profile")
    public ResponseEntity<UserResponse> updateProfile(@Validated @RequestBody UserDto userDto, Authentication authentication) throws Exception{
        return iUserService.updateProfile(userDto,authentication);
    }

    @Operation(summary = "Login User", description = "Login User")
    @PostMapping("/login")
    public ResponseEntity<?> login(@Validated @RequestBody LoginDto loginDto){
        return iUserService.login(loginDto);
    }

    @Operation(summary = "Register User", description = "Register User")
    @PostMapping("/register")
    public ResponseEntity<Notice> registerUser(@Validated @RequestBody RegistrationDto registrationDto) throws Exception {
        return iUserService.registerUser(registrationDto);
    }

    @Operation(summary = "Active User", description = "Active User")
    @PutMapping("/active_account")
    public ResponseEntity<Notice> activeUser(@Validated @RequestBody UserRequestDto userRequestDto){
        return iUserService.activeUser(userRequestDto.getEmail());
    }

    @Operation(summary = "Validate Otp", description = "Validate Otp")
    @PostMapping("/validate_otp")
    public ResponseEntity<?> validateOtp(@Validated @RequestBody ValidateOtpDto validateOtpDto){
        return iUserService.validOtp(validateOtpDto.getOtp(),validateOtpDto.getEmail());
    }

}
