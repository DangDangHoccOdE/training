package com.hoanghaidang.social_network.dto.response;

import com.hoanghaidang.social_network.enums.GenderEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
    private String firstName;
    private String lastName;
    private String address;
    private String job;
    private Date dateOfBirth;
    private GenderEnum gender;
    private String avatar;
}
