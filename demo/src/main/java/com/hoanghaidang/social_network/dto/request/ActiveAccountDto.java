package com.hoanghaidang.social_network.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ActiveAccountDto {
    @NotBlank(message = "Otp is required")
    private String token;

    @NotBlank(message = "Email is not empty")
    @Email(message = "Email is not in the correct format")
    private String email;
}
