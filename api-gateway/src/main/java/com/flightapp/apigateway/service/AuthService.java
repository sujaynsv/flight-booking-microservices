package com.flightapp.apigateway.service;

import com.flightapp.apigateway.dto.AuthResponse;
import com.flightapp.apigateway.dto.LoginRequest;
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

@Service
public class AuthService {
    
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }
    
    public Mono<UserInfo> register(RegisterRequest request) {
        log.info("Registering new user: {}", request.getEmail());
        
        return userRepository.existsByEmail(request.getEmail())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new RuntimeException("Email already exists"));
                    }
                    
                    User user = User.builder()
                            .email(request.getEmail())
                            .password(passwordEncoder.encode(request.getPassword()))
                            .firstName(request.getFirstName())
                            .lastName(request.getLastName())
                            .role("USER")
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build();
                    
                    return userRepository.save(user)
                            .map(savedUser -> {
                                String token = jwtService.generateToken(savedUser.getEmail(), savedUser.getRole());
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
    
    public Mono<UserInfo> login(LoginRequest request) {
        log.info("User login attempt: {}", request.getEmail());
        
        return userRepository.findByEmail(request.getEmail())
                .switchIfEmpty(Mono.error(new RuntimeException("Invalid email or password")))
                .flatMap(user -> {
                    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                        return Mono.error(new RuntimeException("Invalid email or password"));
                    }
                    
                    String token = jwtService.generateToken(user.getEmail(), user.getRole());
                    return Mono.just(UserInfo.builder()
                            .email(user.getEmail())
                            .firstName(user.getFirstName())
                            .lastName(user.getLastName())
                            .token(token)
                            .role(user.getRole())
                            .build());
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
}
