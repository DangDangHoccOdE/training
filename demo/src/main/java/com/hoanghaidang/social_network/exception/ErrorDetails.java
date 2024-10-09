package com.hoanghaidang.social_network.exception;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NotNull
@AllArgsConstructor
public class ErrorDetails {
    private int status;
    private LocalDateTime timestamp;
    private String message;
    private String details;
}
