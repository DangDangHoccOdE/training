package com.hoanghaidang.social_network.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestForgetPasswordDto {
    @NotBlank(message = "Email is not empty")
    @Email(message = "Email is not in the correct format")
    @Size(max = 30, message = "Email must not exceed 30 characters")
    private String email;

    @NotBlank(message = "Token is not empty")
    private String token;

    @NotBlank(message = "Password is not empty")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "Password must contain at least 8 characters, including one uppercase letter, one lowercase letter, one number, and one special character (@$!%*?&)."
    )
    @Size(max = 20, message = "Password must not exceed 20 characters")
    private String newPassword;
}
