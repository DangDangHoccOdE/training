package com.luvina.training_final.Spring.boot.project.controller;

import com.luvina.training_final.Spring.boot.project.dto.AccountDto;
import com.luvina.training_final.Spring.boot.project.dto.RegistrationDto;
import com.luvina.training_final.Spring.boot.project.service.inter.IUserEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private IUserEntityService iUserEntityService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Validated @RequestBody AccountDto accountDto){
        return iUserEntityService.login(accountDto);
    }
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Validated @RequestBody RegistrationDto registrationDto) throws Exception {
        return iUserEntityService.registerUser(registrationDto);
    }

    @PutMapping("/active_account/{email}")
    public ResponseEntity<?> activeAccount(@PathVariable String email){
        return iUserEntityService.activeAccount(email);
    }

    @PostMapping("/validate-otp")
    public ResponseEntity<?> validateOtp(@RequestParam("otp") String otp,@Validated @RequestBody AccountDto accountDto){
        return iUserEntityService.validOtp(otp,accountDto);
    }
}
