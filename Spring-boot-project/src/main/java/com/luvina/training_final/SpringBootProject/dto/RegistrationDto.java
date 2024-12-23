package com.luvina.training_final.SpringBootProject.dto;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationDto {
    @Valid
    private AccountDto account;

    @Valid
    private UserDto user;}
