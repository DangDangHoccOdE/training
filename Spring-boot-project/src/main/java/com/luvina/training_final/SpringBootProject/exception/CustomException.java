package com.luvina.training_final.SpringBootProject.exception;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class CustomException extends RuntimeException{
    private HttpStatus status;
    public CustomException(String message, HttpStatus code) {
            super(message);
            this.status = code;
    }
}
