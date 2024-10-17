package com.hoanghaidang.social_network.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRequestDto {
    @Email
    @NotBlank(message = "Email is required")
    private String email;
}
