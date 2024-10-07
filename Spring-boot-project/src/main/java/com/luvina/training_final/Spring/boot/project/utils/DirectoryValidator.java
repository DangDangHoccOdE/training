package com.luvina.training_final.Spring.boot.project.utils;

import com.luvina.training_final.Spring.boot.project.annotation.ValidDirectory;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DirectoryValidator implements ConstraintValidator<ValidDirectory, String> {
    @Override
    public void initialize(ValidDirectory constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String avatarPath, ConstraintValidatorContext context) {
        if (avatarPath == null || avatarPath.isEmpty()) {
            return false;
        }

        // Kiểm tra avatarPath có hợp lệ theo yêu cầu của bạn không (vd: bắt đầu bằng /uploads)
        return avatarPath.startsWith("/uploads/") && (avatarPath.endsWith(".jpg")
                    || avatarPath.endsWith(".jpeg")
                    || avatarPath.endsWith(".gif")
                    || avatarPath.endsWith(".png"));
    }
}

