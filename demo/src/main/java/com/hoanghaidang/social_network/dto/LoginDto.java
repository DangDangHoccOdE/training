package com.hoanghaidang.social_network.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginDto {
    @NotBlank(message = "Email is not empty")
    @Email(message = "Email is not in the correct format")
    private String email;

    @NotBlank(message = "Password is not empty")
    private String password;
}
