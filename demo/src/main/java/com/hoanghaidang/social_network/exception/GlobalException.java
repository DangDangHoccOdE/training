package com.hoanghaidang.social_network.exception;

import com.hoanghaidang.social_network.dto.response.ApiResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestControllerAdvice
public class GlobalException extends ResponseEntityExceptionHandler {
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentialsException(BadCredentialsException ex, WebRequest request) {
        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .message("Email or Password is incorrect")
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse);

    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<Void>> handleRuntimeException(CustomException customException) {
            ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                    .status(customException.getStatus().value())
                    .message(customException.getMessage())
                    .build();
        return ResponseEntity.status(customException.getStatus()).body(apiResponse);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(AccessDeniedException ex,WebRequest request) {
        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .status(HttpStatus.FORBIDDEN.value())
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiResponse);
    }

    @ExceptionHandler(ExpiredJwtException.class) // Token hết hạn
    public ResponseEntity<ApiResponse<Void>> handleExpiredJwtException(ExpiredJwtException e){
        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .status(HttpStatus.FORBIDDEN.value())
                .message("Token is expired")
                .build();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiResponse);
    }

    @ExceptionHandler(UnsupportedJwtException.class) // Token không hỗ trợ
    public ResponseEntity<ApiResponse<Void>> handleUnsupportedJwtException(UnsupportedJwtException e){
        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .status(HttpStatus.FORBIDDEN.value())
                .message("Token is unsupported")
                .build();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiResponse);
    }
    @ExceptionHandler(MalformedJwtException.class) // token không đúng định dạng
    public ResponseEntity<ApiResponse<Void>> handleMalformedJwtException(MalformedJwtException e){
        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .status(HttpStatus.FORBIDDEN.value())
                .message("Token is malformed")
                .build();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiResponse);
    }
    @ExceptionHandler(SignatureException.class) // Chữ ký token không đúng
    public ResponseEntity<ApiResponse<Void>> handleSignatureException(SignatureException e){
        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .status(HttpStatus.FORBIDDEN.value())
                .message("SignatureToken is not valid")
                .build();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class) // Token không hợp lệ
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException e){
        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .status(HttpStatus.FORBIDDEN.value())
                .message("Info is not valid")
                .build();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiResponse);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        HashMap<String, List<String>> errors = new HashMap<>();
        ex.getFieldErrors().forEach(
                error -> errors.computeIfAbsent(error.getField(), key->new ArrayList<>()).add(error.getDefaultMessage()));

        ErrorDetails errorDetails = new ErrorDetails(HttpStatus.BAD_REQUEST.value(),LocalDateTime.now(),errors,request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);    }
}
