package com.hoanghaidang.social_network.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ValidateOtpDto {
    @NotBlank(message = "Otp is required")
    @Pattern(regexp = "\\d{6}", message = "Otp must contain exactly 6 digits")
    private String otp;

    @NotBlank(message = "Email is not empty")
    @Email(message = "Email is not in the correct format")
    private String email;
}
