package com.hoanghaidang.social_network.dto.request;
import com.hoanghaidang.social_network.enums.GenderEnum;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistrationDto {
    @NotBlank(message = "First name is required")
    @Size(max = 20, message = "First name must not exceed 20 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 20, message = "Last name must not exceed 20 characters")
    private String lastName;

    private GenderEnum gender;

    @NotNull(message = "Date of birth cannot be null")
    @Pattern(regexp = "^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[0-2])/(19|20)\\d{2}$",message = "Date is not format dd/MM/yyyy or Date is not valid")
    private String dateOfBirth;

    @NotBlank(message = "Email is not empty")
    @Email(message = "Email is not in the correct format")
    @Size(max = 30, message = "Email must not exceed 30 characters")
    private String email;

    @NotBlank(message = "Password is not empty")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "Password must contain at least 8 characters, including one uppercase letter, one lowercase letter, one number, and one special character (@$!%*?&)."
    )
    @Size(max = 20, message = "Password must not exceed 20 characters")
    private String password;
}
