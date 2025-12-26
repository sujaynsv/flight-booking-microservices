package com.flightapp.apigateway.service;

import com.flightapp.apigateway.dto.AuthResponse;
import com.flightapp.apigateway.dto.LoginRequest;
import com.flightapp.apigateway.dto.PasswordResetRequest;
import com.flightapp.apigateway.dto.RegisterRequest;
import com.flightapp.apigateway.dto.UserInfo;
import com.flightapp.apigateway.model.User;
import com.flightapp.apigateway.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {
    
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AccountLockoutService accountLockoutService;
    
    public AuthService(UserRepository userRepository, 
                      PasswordEncoder passwordEncoder, 
                      JwtService jwtService, 
                      AccountLockoutService accountLockoutService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.accountLockoutService = accountLockoutService;
    }
    
    public Mono<UserInfo> register(RegisterRequest request) {
        log.info("Registering new user: {}", request.getEmail());
        
        return userRepository.existsByEmail(request.getEmail())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new RuntimeException("Email already exists"));
                    }
                    
                    String role = request.getRole();
                    
                    User user = User.builder()
                            .email(request.getEmail())
                            .password(passwordEncoder.encode(request.getPassword()))
                            .firstName(request.getFirstName())
                            .lastName(request.getLastName())
                            .role(role)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .lastPasswordChange(LocalDateTime.now())
                            .failedLoginAttempts(0)
                            .accountLocked(false)
                            .build();
                    
                    return userRepository.save(user)
                            .map(savedUser -> {
                                String token = jwtService.generateToken(
                                    savedUser.getEmail(), 
                                    savedUser.getRole(), 
                                    savedUser.getLastPasswordChange()
                                );
                                return UserInfo.builder()
                                        .email(savedUser.getEmail())
                                        .firstName(savedUser.getFirstName())
                                        .lastName(savedUser.getLastName())
                                        .token(token)
                                        .role(savedUser.getRole())
                                        .build();
                            });
                });
    }
    
    public Mono<Map<String, String>> login(LoginRequest request) {
        return userRepository.findByEmail(request.getEmail())
                .switchIfEmpty(Mono.error(new RuntimeException("Invalid credentials")))
                .flatMap(user -> {
                    // Check if account is locked
                    return accountLockoutService.isAccountLocked(user.getEmail())
                        .flatMap(isLocked -> {
                            if (isLocked) {
                                return accountLockoutService.getRemainingLockoutTime(user.getEmail())
                                    .flatMap(lockoutUntil -> {
                                        if (lockoutUntil != null) {
                                            long minutesRemaining = ChronoUnit.MINUTES.between(
                                                LocalDateTime.now(), 
                                                lockoutUntil
                                            );
                                            
                                            if (minutesRemaining > 0) {
                                                log.warn("Login attempt on locked account: {}", user.getEmail());
                                                return Mono.error(new RuntimeException(
                                                    "Account is locked. Please try again in " + 
                                                    minutesRemaining + " minute(s)."
                                                ));
                                            } else {
                                                // Lockout expired, reset and allow login
                                                return accountLockoutService.resetFailedLoginAttempts(user.getEmail())
                                                    .then(proceedWithLogin(request, user));
                                            }
                                        }
                                        return proceedWithLogin(request, user);
                                    });
                            }
                            
                            return proceedWithLogin(request, user);
                        });
                });
    }
    
    private Mono<Map<String, String>> proceedWithLogin(LoginRequest request, User user) {
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Invalid password for user: {}", user.getEmail());
            
            // Record failed attempt
            return accountLockoutService.recordFailedLoginAttempt(user.getEmail())
                .flatMap(updatedUser -> {
                    if (updatedUser.getAccountLocked() != null && updatedUser.getAccountLocked()) {
                        return Mono.error(new RuntimeException(
                            "Too many failed login attempts. Account locked for 10 minutes. " +
                            "A notification email has been sent to " + user.getEmail()
                        ));
                    }
                    
                    int attemptsLeft = 4 - (updatedUser.getFailedLoginAttempts() != null ? 
                        updatedUser.getFailedLoginAttempts() : 0);
                    
                    return Mono.error(new RuntimeException(
                        "Invalid credentials. " + attemptsLeft + " attempt(s) remaining before account lockout."
                    ));
                });
        }
        
        // Password correct - reset failed attempts
        return accountLockoutService.resetFailedLoginAttempts(user.getEmail())
            .flatMap(updatedUser -> {
                // Check password expiry
                if (updatedUser.getLastPasswordChange() != null) {
                    log.info("Checking password expiry for user: {}", updatedUser.getEmail());
                    LocalDateTime expiryDate = updatedUser.getLastPasswordChange().plusDays(30);
                    if (LocalDateTime.now().isAfter(expiryDate)) {
                        log.warn("Password expired for user: {}", updatedUser.getEmail());
                        return Mono.error(new RuntimeException("Password has expired. Please reset your password"));
                    }
                }
                
                String token = jwtService.generateToken(
                    updatedUser.getEmail(), 
                    updatedUser.getRole(),
                    updatedUser.getLastPasswordChange()
                );
                
                Map<String, String> response = new HashMap<>();
                response.put("token", token);
                response.put("email", updatedUser.getEmail());
                response.put("role", updatedUser.getRole());
                response.put("firstname", updatedUser.getFirstName());
                response.put("lastname", updatedUser.getLastName());
                
                log.info("User logged in successfully: {}", updatedUser.getEmail());
                return Mono.just(response);
            });
    }

    public Mono<AuthResponse> getUserInfo(String email) {
        return userRepository.findByEmail(email)
                .map(user -> AuthResponse.builder()
                        .email(user.getEmail())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .role(user.getRole())
                        .message("User details fetched successfully")
                        .build());
    }
    
    public Mono<Boolean> changePassword(String email, String currentPassword, String newPassword) {
        log.info("Password change attempt for user: {}", email);
        
        return userRepository.findByEmail(email)
            .switchIfEmpty(Mono.error(new RuntimeException("User not found")))
            .flatMap(user -> {
                if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
                    log.warn("Invalid current password for user: {}", email);
                    return Mono.just(false);
                }
                
                // Update password (hash the new one)
                user.setPassword(passwordEncoder.encode(newPassword));
                user.setUpdatedAt(LocalDateTime.now());
                user.setLastPasswordChange(LocalDateTime.now());
                
                return userRepository.save(user)
                    .map(savedUser -> {
                        log.info("Password changed successfully for user: {}", email);
                        return true;
                    });
            });
    }
    
    public Mono<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Mono<Map<String, String>> resetPasswordWithCredentials(PasswordResetRequest request) {
        return userRepository.findByEmail(request.getEmail())
                .switchIfEmpty(Mono.error(new RuntimeException("User not found")))
                .flatMap(user -> {
                    // Verify current password
                    if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                        return Mono.error(new RuntimeException("Current password is incorrect"));
                    }
                    
                    // Check if new password is same as old
                    if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
                        return Mono.error(new RuntimeException("New password must be different from current password"));
                    }
                    
                    // Update password and lastPasswordChange
                    user.setPassword(passwordEncoder.encode(request.getNewPassword()));
                    user.setLastPasswordChange(LocalDateTime.now());
                    user.setUpdatedAt(LocalDateTime.now());
                    
                    return userRepository.save(user)
                            .map(savedUser -> {
                                Map<String, String> response = new HashMap<>();
                                response.put("message", "Password reset successfully");
                                log.info("Password reset successfully for user: {}", user.getEmail());
                                return response;
                            });
                });
    }
}
