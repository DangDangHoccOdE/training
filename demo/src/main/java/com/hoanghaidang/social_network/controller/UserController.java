package com.hoanghaidang.social_network.controller;


import com.hoanghaidang.social_network.dto.LoginDto;
import com.hoanghaidang.social_network.dto.RegistrationDto;
import com.hoanghaidang.social_network.dto.RequestForgetPasswordDto;
import com.hoanghaidang.social_network.dto.UserDto;
import com.hoanghaidang.social_network.service.inter.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private IUserService iUserService;
    @PostMapping("/forget_password/{id}")
    public ResponseEntity<?> forgetPassword(@PathVariable long id){
        return iUserService.forgetPassword(id);
    }

    @PutMapping("/change_password/{id}")
    public ResponseEntity<?> changePassword(@PathVariable long id,@Validated @RequestBody RequestForgetPasswordDto requestForgetPasswordDto){
        return iUserService.changePassword(id,requestForgetPasswordDto.getToken(),requestForgetPasswordDto.getNewPassword());
    }

    @PutMapping("/update_profile/{id}")
    public ResponseEntity<?> updateProfile(@PathVariable long id,@Validated @RequestBody UserDto userDto) throws Exception{
        return iUserService.updateProfile(id,userDto);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Validated @RequestBody LoginDto loginDto){
        return iUserService.login(loginDto);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Validated @RequestBody RegistrationDto registrationDto) throws Exception {
        return iUserService.registerUser(registrationDto);
    }

    @PutMapping("/active_account/{email}")
    public ResponseEntity<?> activeAccount(@PathVariable String email){
        return iUserService.activeUser(email);
    }

    @PostMapping("/validate_otp")
    public ResponseEntity<?> validateOtp(@RequestParam("otp") String otp,@Validated @RequestBody LoginDto loginDto){
        return iUserService.validOtp(otp,loginDto);
    }

}
