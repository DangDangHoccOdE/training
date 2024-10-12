package com.hoanghaidang.social_network.controller;

import com.hoanghaidang.social_network.dto.*;
import com.hoanghaidang.social_network.service.inter.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/user")
@Tag(name = "User Management", description = "APIs for managing user accounts and authentication")
public class UserController {
    @Autowired
    private IUserService iUserService;

    @Operation(summary = "Refresh Token",description = "Refresh Token")
    @PostMapping("/refreshToken")
    public ResponseEntity<?> refreshToken(@RequestHeader String refreshToken){
        return iUserService.refreshToken(refreshToken);
    }

    @Operation(summary = "Report User",description = "Report User during a week")
    @GetMapping("/report")
    public ResponseEntity<?> report(Authentication authentication) throws IOException {
        return iUserService.report(authentication.getName());
    }

    @Operation(summary = "Forget Password", description = "Forget Password")
    @PostMapping("/forget_password")
    public ResponseEntity<?> forgetPassword(@Validated @RequestBody UserRequestDto userRequestDto){
        return iUserService.forgetPassword(userRequestDto.getEmail());
    }

    @Operation(summary = "Change Password", description = "Change Password")
    @PutMapping("/change_password/{email}")
    public ResponseEntity<?> changePassword(@PathVariable String email,@Validated @RequestBody RequestForgetPasswordDto requestForgetPasswordDto){
        return iUserService.changePassword(email,requestForgetPasswordDto.getToken(),requestForgetPasswordDto.getNewPassword());
    }

    @Operation(summary = "Update Profile", description = "Update Profile for User")
    @PutMapping("/update_profile/{email}")
    public ResponseEntity<?> updateProfile(@PathVariable String email,@Validated @RequestBody UserDto userDto,Authentication authentication) throws Exception{
        return iUserService.updateProfile(email,userDto,authentication);
    }

    @Operation(summary = "Login User", description = "Login User")
    @PostMapping("/login")
    public ResponseEntity<?> login(@Validated @RequestBody LoginDto loginDto){
        return iUserService.login(loginDto);
    }

    @Operation(summary = "Register User", description = "Register User")
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Validated @RequestBody RegistrationDto registrationDto) throws Exception {
        return iUserService.registerUser(registrationDto);
    }

    @Operation(summary = "Active User", description = "Active User")
    @PutMapping("/active_account/{email}")
    public ResponseEntity<?> activeUser(@PathVariable String email){
        return iUserService.activeUser(email);
    }

    @Operation(summary = "Validate Otp", description = "Validate Otp")
    @PostMapping("/validate_otp")
    public ResponseEntity<?> validateOtp(@Validated @RequestBody ValidateOtpDto validateOtpDto){
        return iUserService.validOtp(validateOtpDto.getOtp(),validateOtpDto.getEmail());
    }

}
