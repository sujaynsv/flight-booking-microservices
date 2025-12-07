package com.flightapp.apigateway.controller;

import com.flightapp.apigateway.dto.AuthResponse;
import com.flightapp.apigateway.dto.LoginRequest;
import com.flightapp.apigateway.dto.RegisterRequest;
import com.flightapp.apigateway.service.AuthService;
import com.flightapp.apigateway.service.JwtService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
@RequestMapping("/auth")
public class AuthController {
    
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    
    private final AuthService authService;
    private final JwtService jwtService;
    
    @Value("${jwt.cookie.name}")
    private String cookieName;
    
    @Value("${jwt.cookie.max-age}")
    private Long cookieMaxAge;
    
    public AuthController(AuthService authService, JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }
    
    @PostMapping("/register")
    public Mono<ResponseEntity<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Register request received for email: {}", request.getEmail());
        
        return authService.register(request)
                .flatMap(token -> {
                    ResponseCookie cookie = createCookie(token);
                    
                    return authService.getUserInfo(request.getEmail())
                            .map(authResponse -> {
                                authResponse.setMessage("Registration successful");
                                return ResponseEntity.ok()
                                        .header(HttpHeaders.SET_COOKIE, cookie.toString())
                                        .body(authResponse);
                            });
                })
                .onErrorResume(e -> {
                    log.error("Registration failed: {}", e.getMessage());
                    AuthResponse errorResponse = AuthResponse.builder()
                            .message(e.getMessage())
                            .build();
                    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse));
                });
    }
    
    @PostMapping("/login")
    public Mono<ResponseEntity<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login request received for email: {}", request.getEmail());
        
        return authService.login(request)
                .flatMap(token -> {
                    ResponseCookie cookie = createCookie(token);
                    
                    return authService.getUserInfo(request.getEmail())
                            .map(authResponse -> {
                                authResponse.setMessage("Login successful");
                                return ResponseEntity.ok()
                                        .header(HttpHeaders.SET_COOKIE, cookie.toString())
                                        .body(authResponse);
                            });
                })
                .onErrorResume(e -> {
                    log.error("Login failed: {}", e.getMessage());
                    AuthResponse errorResponse = AuthResponse.builder()
                            .message(e.getMessage())
                            .build();
                    return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse));
                });
    }
    
    @PostMapping("/logout")
    public Mono<ResponseEntity<AuthResponse>> logout() {
        log.info("Logout request received");
        
        ResponseCookie cookie = ResponseCookie.from(cookieName, "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .build();
        
        AuthResponse response = AuthResponse.builder()
                .message("Logout successful")
                .build();
        
        return Mono.just(ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(response));
    }
    
    @GetMapping("/me")
    public Mono<ResponseEntity<AuthResponse>> getCurrentUser(ServerHttpRequest request) {
        String token = extractTokenFromCookie(request);
        
        if (token == null || !jwtService.validateToken(token)) {
            AuthResponse errorResponse = AuthResponse.builder()
                    .message("Unauthorized")
                    .build();
            return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse));
        }
        
        String email = jwtService.extractEmail(token);
        return authService.getUserInfo(email)
                .map(ResponseEntity::ok);
    }
    
    private ResponseCookie createCookie(String token) {
        return ResponseCookie.from(cookieName, token)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(Duration.ofSeconds(cookieMaxAge))
                .sameSite("Lax")
                .build();
    }
    
    private String extractTokenFromCookie(ServerHttpRequest request) {
        HttpCookie cookie = request.getCookies().getFirst(cookieName);
        return cookie != null ? cookie.getValue() : null;
    }
}
