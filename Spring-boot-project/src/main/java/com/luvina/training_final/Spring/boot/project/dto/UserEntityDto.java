package com.luvina.training_final.Spring.boot.project.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEntityDto {
    private long id;

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

    @NotNull
    @Pattern(regexp = "^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[0-2])/(\\d{4})$", message = "Date of birth must be in the format dd/MM/yyyy")
    private String dateOfBirth;

    @Size(max = 100, message = "Job must not exceed 100 characters")
    private String job;

    @URL(message = "Facebook link must be a valid URL")
    private String facebookLink;

    @URL(message = "Avatar must be a valid URL")
    private String avatar;

}
