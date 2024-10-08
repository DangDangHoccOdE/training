package com.luvina.training_final.SpringBootProject.utils;

import com.luvina.training_final.SpringBootProject.annotation.ValidDirectory;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DirectoryValidator implements ConstraintValidator<ValidDirectory, String> {
    @Override
    public void initialize(ValidDirectory constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String avatarPath, ConstraintValidatorContext context) {
        // Cho phép avatarPath null hoặc rỗng
        if (avatarPath == null || avatarPath.isEmpty()) {
            return true;
        }

        // Kiểm tra avatarPath có hợp lệ theo yêu cầu của bạn
        return avatarPath.startsWith("/uploads/") && (avatarPath.endsWith(".jpg")
                || avatarPath.endsWith(".jpeg")
                || avatarPath.endsWith(".gif")
                || avatarPath.endsWith(".png"));
    }
}

