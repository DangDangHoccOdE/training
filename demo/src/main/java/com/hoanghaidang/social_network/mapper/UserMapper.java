package com.hoanghaidang.social_network.mapper;

import com.hoanghaidang.social_network.dto.response.UserResponse;
import com.hoanghaidang.social_network.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toUserResponse(User user) {
        UserResponse.UserResponseBuilder responseBuilder = UserResponse.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .gender(user.getGender())
                .dateOfBirth(user.getDateOfBirth());

        // Kiểm tra và set job nếu có
        if (user.getJob() != null && !user.getJob().isEmpty()) {
            responseBuilder.job(user.getJob());
        } else {
            responseBuilder.job("N/A");  // Hoặc set giá trị mặc định như "N/A"
        }

        // Kiểm tra và set address nếu có
        if (user.getAddress() != null && !user.getAddress().isEmpty()) {
            responseBuilder.address(user.getAddress());
        } else {
            responseBuilder.address("N/A");  // Hoặc set giá trị mặc định
        }

        // Kiểm tra và set avatar nếu có
        if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
            responseBuilder.avatar(user.getAvatar());
        } else {
            responseBuilder.avatar(null);  // Set giá trị mặc định cho avatar
        }

        return responseBuilder.build();
    }
}
