package com.hoanghaidang.social_network.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ValidateOtpDto {
    @NotBlank(message = "Otp is required")
    private String otp;

    @NotBlank(message = "Email is required")
    private String email;
}
