package com.luvina.training_final.Spring.boot.project.controller;

import com.luvina.training_final.Spring.boot.project.dto.AccountDto;
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

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Validated @RequestBody AccountDto accountDto){
        return iUserEntityService.registerUser(accountDto);
    }
}
