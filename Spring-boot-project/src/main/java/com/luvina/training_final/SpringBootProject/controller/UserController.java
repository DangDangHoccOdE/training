package com.luvina.training_final.SpringBootProject.controller;

import com.luvina.training_final.SpringBootProject.dto.AccountDto;
import com.luvina.training_final.SpringBootProject.dto.RegistrationDto;
import com.luvina.training_final.SpringBootProject.dto.RequestForgetPasswordDto;
import com.luvina.training_final.SpringBootProject.dto.UserDto;
import com.luvina.training_final.SpringBootProject.service.inter.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private IUserService iUserService;

    @GetMapping("/get_all")
    public ResponseEntity<?> getAllUser() throws Exception {
        return iUserService.getAllUser();
    }

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
    public ResponseEntity<?> login(@Validated @RequestBody AccountDto accountDto){
        return iUserService.login(accountDto);
    }
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Validated @RequestBody RegistrationDto registrationDto) throws Exception {
        return iUserService.registerUser(registrationDto);
    }

    @PutMapping("/active_account/{email}")
    public ResponseEntity<?> activeAccount(@PathVariable String email){
        return iUserService.activeAccount(email);
    }

    @PostMapping("/validate-otp")
    public ResponseEntity<?> validateOtp(@RequestParam("otp") String otp,@Validated @RequestBody AccountDto accountDto){
        return iUserService.validOtp(otp,accountDto);
    }

}
