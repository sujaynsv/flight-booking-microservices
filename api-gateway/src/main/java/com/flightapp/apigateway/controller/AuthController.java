package com.flightapp.apigateway.controller;

import com.flightapp.apigateway.dto.AuthResponse;
import com.flightapp.apigateway.dto.ChangePasswordRequest;
import com.flightapp.apigateway.dto.ForgotPasswordRequest;
import com.flightapp.apigateway.dto.LoginRequest;
import com.flightapp.apigateway.dto.PasswordResetRequest;
import com.flightapp.apigateway.dto.RegisterRequest;
import com.flightapp.apigateway.dto.ResetPasswordRequest;
import com.flightapp.apigateway.service.AuthService;
import com.flightapp.apigateway.service.JwtService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import org.springframework.http.HttpCookie;


import org.springframework.security.core.Authentication;
import java.util.Map;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import com.flightapp.apigateway.service.PasswordResetService;
import java.time.Duration;

@RestController
@RequestMapping("/auth")
//@CrossOrigin(
//		origins="http://localhost:4200",
//		allowCredentials="true",
//		methods={RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS}
//		)
public class AuthController {
    
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    
    private final PasswordResetService passwordResetService ;
    private final AuthService authService;
    private final JwtService jwtService;
    
    @Value("${jwt.cookie.name}")
    private String cookieName;
    
    @Value("${jwt.cookie.max-age}")
    private Long cookieMaxAge;
    
    public AuthController(AuthService authService, JwtService jwtService, PasswordResetService passwordResetService) {
        this.authService = authService;
        this.jwtService = jwtService;
        this.passwordResetService=passwordResetService;
    }   
    
    @PostMapping("/register")
    public Mono<ResponseEntity<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Register request received for email: {}", request.getEmail());
        
        return authService.register(request)
                .flatMap(userInfo -> {
                    ResponseCookie cookie = createCookie(userInfo.getToken());
                    
                    AuthResponse response = AuthResponse.builder()
                            .email(userInfo.getEmail())
                            .firstName(userInfo.getFirstName())  
                            .lastName(userInfo.getLastName())
                            .role(userInfo.getRole())
                            .message("Registration successful")
                            .build();
                    
                    return Mono.just(ResponseEntity.ok()
                            .header(HttpHeaders.SET_COOKIE, cookie.toString())
                            .body(response));
                })
                .onErrorResume(e -> {
                    log.error("Registration failed: {}", e.getMessage());
                    
                    AuthResponse errorResponse = AuthResponse.builder()
                            .message("Registration failed: " + e.getMessage())
                            .build();
                    
                    return Mono.just(ResponseEntity
                            .status(HttpStatus.BAD_REQUEST)
                            .body(errorResponse));
                });
    }
    @PostMapping("/login")
    public Mono<ResponseEntity<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login request received for email: {}", request.getEmail());
        
        return authService.login(request)
                .flatMap(userInfo -> {
                    // ← FIX: Use .get("token") instead of .getToken()
                    ResponseCookie cookie = createCookie(userInfo.get("token"));
                    
                    AuthResponse response = AuthResponse.builder()
                            .email(userInfo.get("email"))           // ← Use .get()
                            .firstName(userInfo.get("firstname"))   // ← Use .get()
                            .lastName(userInfo.get("lastname"))     // ← Use .get()
                            .role(userInfo.get("role"))             // ← Use .get()
                            .message("Login successful")
                            .build();
                    
                    return Mono.just(ResponseEntity.ok()
                            .header(HttpHeaders.SET_COOKIE, cookie.toString())
                            .body(response));
                })
                .onErrorResume(e -> {
                    log.error("Login failed: {}", e.getMessage());
                    
                    AuthResponse errorResponse = AuthResponse.builder()
                            .message("Login failed: " + e.getMessage())
                            .build();
                    
                    return Mono.just(ResponseEntity
                            .status(HttpStatus.UNAUTHORIZED)
                            .body(errorResponse));
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
            return Mono.just(ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(errorResponse));
        }

        String email = jwtService.extractEmail(token);

        return authService.getUserInfo(email)
                .map(userInfo -> {
                    AuthResponse response = AuthResponse.builder()
                            .email(userInfo.getEmail())
                            .firstName(userInfo.getFirstName())
                            .lastName(userInfo.getLastName())
                            .role(userInfo.getRole())      // ← include role
                            .message("User info")
                            .build();
                    return ResponseEntity.ok(response);
                });
    }
    
    
    @PatchMapping("/password")
    public Mono<ResponseEntity<Map<String, String>>> changePassword(
        @RequestBody ChangePasswordRequest request,
        ServerHttpRequest requestObj
    ) {
        // DEBUG: Log ALL headers first
        log.info("All headers: {}", requestObj.getHeaders());
        
        // Try X-User-Email first, then fallback to cookie
        String email = requestObj.getHeaders().getFirst("X-User-Email");
        
        if (email == null || email.isEmpty()) {
            log.info("No X-User-Email header, trying cookie extraction...");
            email = extractEmailFromCookie(requestObj);
        }
        
        log.info("Final extracted email: '{}'", email);
        
        if (email == null || email.isEmpty()) {
            log.warn("No authentication found (header or cookie)");
            return Mono.just(ResponseEntity.status(401)
                .body(Map.of("message", "Not authenticated")));
        }
        
        log.info("Password change request for user: {}", email);
        
        final String userEmail = email;  // ← ADD THIS: Make final for lambda
        
        // Call changePassword service
        return authService.changePassword(userEmail, request.getCurrentPassword(), request.getNewPassword())
            .flatMap(success -> {  // ← CHANGED from .map to .flatMap
                if (!success) {
                    return Mono.just(ResponseEntity.badRequest()
                        .body(Map.of("message", "Current password is incorrect")));
                }
                
                // Password changed - get updated user and generate new token
                return authService.getUserByEmail(userEmail)  // ← ADD THIS: Fetch updated user
                    .map(user -> {  // ← ADD THIS: Generate new token
                        String newToken = jwtService.generateToken(
                            user.getEmail(), 
                            user.getRole(), 
                            user.getLastPasswordChange()
                        );
                        
                        ResponseCookie cookie = createCookie(newToken);
                        
                        Map<String, String> responseBody = new HashMap<>();
                        responseBody.put("message", "Password changed successfully");
                        responseBody.put("token", newToken);
                        
                        return ResponseEntity.ok()
                            .header(HttpHeaders.SET_COOKIE, cookie.toString())
                            .body(responseBody);
                    })
                    .defaultIfEmpty(ResponseEntity.internalServerError()
                        .body(Map.of("message", "Failed to retrieve user data")));
            })
            .onErrorResume(error -> {  // ← CHANGED from onErrorReturn
                log.error("Password change error: {}", error.getMessage());
                return Mono.just(ResponseEntity.internalServerError()
                    .body(Map.of("message", "Password change failed: " + error.getMessage())));
            });
    }


    private String extractEmailFromCookie(ServerHttpRequest request) {
        try {
            HttpCookie authCookie = request.getCookies().getFirst("authToken");
            if (authCookie == null) {
                log.warn("No authToken cookie found");
                return null;
            }
            
            String token = authCookie.getValue();
            if (token == null) {
                log.warn("authToken cookie has no value");
                return null;
            }
            
            log.info("Token found: {}", token.substring(0, 20) + "...");
            String email = jwtService.extractEmail(token);
            log.info("Email extracted from JWT: {}", email);
            return email;
            
        } catch (Exception e) {
            log.error("Cookie extraction failed: {}", e.getMessage());
            return null;
        }
    }


    
//    private String extractEmailFromCookie(ServerHttpRequest request) {
//        try {
//            // Get first JWT cookie
//            HttpCookie jwtCookie = request.getCookies().getFirst("eyJhbGciOiJIUzUxMiJ9.eyJyb2xlIjoiQURNSU4iLCJzdWIiOiJhZG1pbnRlc3Q4QGdtYWlsLmNvbSIsImlhdCI6MTc2NjM5Mzc3NCwiZXhwIjoxNzY2NDgwMTc0fQ.WSjL92hG5foZ8z9Jtcii42TPe6-MattbLbPuH-KyIRd2kYFaBwlax0EGkmVav8F2-ZGczjD8o-YTzgvieMzowQ");  // Your cookie name
//            
//            if (jwtCookie == null) {
//                log.warn("No JWT cookie found");
//                return null;
//            }
//            
//            String token = jwtCookie.getValue();
//            if (token == null) {
//                return null;
//            }
//            
//            // Extract email from JWT
//            return jwtService.extractEmail(token);
//            
//        } catch (Exception e) {
//            log.error("Failed to extract email from cookie: {}", e.getMessage());
//            return null;
//        }
//    }


    
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

    @PatchMapping("/password-reset")
    public Mono<ResponseEntity<Map<String, String>>> resetPasswordWithCredentials(
            @Valid @RequestBody PasswordResetRequest request) {
        log.info("Password reset request for email: {}", request.getEmail());
        
        return authService.resetPasswordWithCredentials(request)
                .map(response -> ResponseEntity.ok(response))
                .onErrorResume(e -> {
                    log.error("Password reset failed: {}", e.getMessage());
                    Map<String, String> errorResponse = new HashMap<>();
                    errorResponse.put("message", e.getMessage());
                    return Mono.just(ResponseEntity
                            .status(HttpStatus.BAD_REQUEST)
                            .body(errorResponse));
                });
    }

        @PostMapping("/forgot-password")
    public Mono<ResponseEntity<Map<String, String>>> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        return passwordResetService.forgotPassword(request)
                .map(message -> ResponseEntity.ok(Map.of("message", message)))
                .onErrorResume(error -> Mono.just(
                    ResponseEntity.badRequest()
                        .body(Map.of("error", error.getMessage()))
                ));
    }
    
    @PostMapping("/reset-password")
    public Mono<ResponseEntity<Map<String, String>>> resetPassword(@RequestBody ResetPasswordRequest request) {
        return passwordResetService.resetPassword(request)
                .map(message -> ResponseEntity.ok(Map.of("message", message)))
                .onErrorResume(error -> Mono.just(
                    ResponseEntity.badRequest()
                        .body(Map.of("error", error.getMessage()))
                ));
    }


}
