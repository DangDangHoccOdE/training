package com.hoanghaidang.social_network.exception;

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
