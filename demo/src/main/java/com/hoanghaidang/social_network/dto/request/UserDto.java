package com.hoanghaidang.social_network.dto.request;

import com.hoanghaidang.social_network.enums.GenderEnum;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    @NotBlank(message = "First name is required")
    @Size(max = 20, message = "First name must not exceed 20 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 20, message = "Last name must not exceed 20 characters")
    private String lastName;

    @Size(max = 255, message = "Address must not exceed 255 characters")
    private String address;

    private GenderEnum gender;

    @NotNull(message = "Date of birth cannot be null")
    @Pattern(regexp = "^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[0-2])/(19|20)\\d{2}$",message = "Date is not format dd/MM/yyyy")
    private String dateOfBirth;

    @Size(max = 100, message = "Job must not exceed 100 characters")
    private String job;

    private String avatar;
}
