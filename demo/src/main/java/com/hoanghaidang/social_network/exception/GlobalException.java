package com.hoanghaidang.social_network.exception;

import com.hoanghaidang.social_network.entity.Notice;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
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

import java.security.SignatureException;
import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalException extends ResponseEntityExceptionHandler {
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentialsException(BadCredentialsException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(HttpStatus.UNAUTHORIZED.value(),LocalDateTime.now(),
                ex.getMessage() + " Email or Password is incorrect", request.getDescription(false));

        return new ResponseEntity<>(errorDetails, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<?> handleRuntimeException(CustomException customException) {
        return new ResponseEntity<>(new Notice(customException.getMessage()), customException.getStatus() );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDenied(AccessDeniedException ex,WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(HttpStatus.FORBIDDEN.value(), LocalDateTime.now(),
                ex.getMessage(),request.getDescription(false));

        return new ResponseEntity<>(errorDetails, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ExpiredJwtException.class) // Token hết hạn
    public ResponseEntity<Notice> handleExpiredJwtException(ExpiredJwtException e){
        System.out.println("ExpiredJwtException: "+e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Notice("Token đã hết hạn"));
    }

    @ExceptionHandler(MalformedJwtException.class) // token không đúng định dạng
    public ResponseEntity<?> handleMalformedJwtException(MalformedJwtException e){
        System.out.println("MalformedJwtException: "+e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Notice("Token không đúng định dạng"));
    }
    @ExceptionHandler(SignatureException.class) // Chữ ký token không đúng
    public ResponseEntity<?> handleSignatureException(SignatureException e){
        System.out.println("SignatureException: "+e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Notice("Chữ ký token không hợp lệ"));
    }

    @ExceptionHandler(IllegalArgumentException.class) // Token không hợp lệ
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException e){
        System.out.println("IllegalArgumentException: "+e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Notice("Token không hợp lệ"));
    }
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(HttpStatus.BAD_REQUEST.value(),LocalDateTime.now(),
                "Total Errors:" + ex.getErrorCount() + " First Error " + ex.getFieldError().getDefaultMessage(),request.getDescription(false));

        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);    }
}
