package com.hoanghaidang.social_network.controller;


import com.hoanghaidang.social_network.dto.LoginDto;
import com.hoanghaidang.social_network.dto.RegistrationDto;
import com.hoanghaidang.social_network.dto.RequestForgetPasswordDto;
import com.hoanghaidang.social_network.dto.UserDto;
import com.hoanghaidang.social_network.entity.User;
import com.hoanghaidang.social_network.exception.ErrorDetails;
import com.hoanghaidang.social_network.service.inter.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@Tag(name = "User Management", description = "APIs for managing user accounts and authentication")
public class UserController {
    @Autowired
    private IUserService iUserService;

    @Operation(summary = "Forget Password", description = "Forget Password")
    @PostMapping("/forget_password/{id}")
    public ResponseEntity<?> forgetPassword(@PathVariable long id){
        return iUserService.forgetPassword(id);
    }

    @Operation(summary = "Change Password", description = "Change Password")
    @PutMapping("/change_password/{id}")
    public ResponseEntity<?> changePassword(@PathVariable long id,@Validated @RequestBody RequestForgetPasswordDto requestForgetPasswordDto){
        return iUserService.changePassword(id,requestForgetPasswordDto.getToken(),requestForgetPasswordDto.getNewPassword());
    }

    @Operation(summary = "Update Profile", description = "Update Profile for User")
    @PutMapping("/update_profile/{id}")
    public ResponseEntity<?> updateProfile(@PathVariable long id,@Validated @RequestBody UserDto userDto) throws Exception{
        return iUserService.updateProfile(id,userDto);
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
    public ResponseEntity<?> validateOtp(@RequestParam("otp") String otp, @RequestParam("email") String email){
        return iUserService.validOtp(otp,email);
    }

}
