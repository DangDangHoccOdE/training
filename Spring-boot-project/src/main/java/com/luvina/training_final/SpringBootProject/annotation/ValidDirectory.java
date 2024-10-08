package com.luvina.training_final.SpringBootProject.annotation;

import com.luvina.training_final.SpringBootProject.utils.DirectoryValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = DirectoryValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDirectory {
    String message() default "Avatar path must be a valid directory";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
