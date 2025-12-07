package com.flightapp.apigateway.exception;

import com.flightapp.apigateway.dto.AuthResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    @ExceptionHandler(UnauthorizedException.class)
    public Mono<ResponseEntity<AuthResponse>> handleUnauthorizedException(UnauthorizedException ex) {
        log.error("Unauthorized error: {}", ex.getMessage());
        AuthResponse response = AuthResponse.builder()
                .message(ex.getMessage())
                .build();
        return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response));
    }
    
    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<AuthResponse>> handleValidationException(WebExchangeBindException ex) {
        String message = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        log.error("Validation error: {}", message);
        AuthResponse response = AuthResponse.builder()
                .message(message)
                .build();
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response));
    }
    
    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<AuthResponse>> handleGenericException(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        AuthResponse response = AuthResponse.builder()
                .message("An unexpected error occurred")
                .build();
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response));
    }
}
