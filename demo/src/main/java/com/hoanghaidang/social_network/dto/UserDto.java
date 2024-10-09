package com.hoanghaidang.social_network.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hoanghaidang.social_network.annotation.ValidDirectory;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    @NotBlank(message = "First name is required")
    @Size(max = 20, message = "First name must not exceed 20 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 20, message = "Last name must not exceed 20 characters")
    private String lastName;

    @Size(max = 255, message = "Address must not exceed 255 characters")
    private String address;

    @Pattern(regexp = "Nam|Nữ", message = "Gender must be 'Nam', 'Nữ'")
    private String gender;

    @NotNull(message = "Date of birth cannot be null")
    @Past(message = "Date of birth must be a date in the past")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date dateOfBirth;

    @Size(max = 100, message = "Job must not exceed 100 characters")
    private String job;

    @ValidDirectory(message = "Avatar path is not valid")
    private String avatar;
}
