package com.flightapp.apigateway.service;

import com.flightapp.apigateway.dto.EmailNotification;
import com.flightapp.apigateway.dto.ForgotPasswordRequest;
import com.flightapp.apigateway.dto.ResetPasswordRequest;
import com.flightapp.apigateway.model.PasswordResetToken;
import com.flightapp.apigateway.repository.PasswordResetTokenRepository;
import com.flightapp.apigateway.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class PasswordResetService {
    
    private static final Logger log = LoggerFactory.getLogger(PasswordResetService.class);
    
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    
    public PasswordResetService(UserRepository userRepository,
                                PasswordResetTokenRepository passwordResetTokenRepository,
                                PasswordEncoder passwordEncoder,
                                EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }
    
    public Mono<String> forgotPassword(ForgotPasswordRequest request) {
        log.info("Processing forgot password for email: {}", request.getEmail());
        
        return userRepository.findByEmail(request.getEmail())
                .switchIfEmpty(Mono.error(new RuntimeException("User not found with email: " + request.getEmail())))
                .flatMap(user -> {
                    String code = generateSixDigitCode();
                    LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(15);
                    
                    PasswordResetToken token = new PasswordResetToken(
                        request.getEmail(),
                        code,
                        expiryTime
                    );
                    
                    return passwordResetTokenRepository.deleteByEmail(request.getEmail())
                            .then(passwordResetTokenRepository.save(token))
                            .flatMap(savedToken -> {
                                EmailNotification emailNotification = new EmailNotification(
                                    request.getEmail(),
                                    "Password Reset Code",
                                    buildPasswordResetEmail(code)
                                );
                                
                                return emailService.sendEmail(emailNotification)
                                        .thenReturn("Password reset code sent to your email");
                            });
                });
    }
    
    public Mono<String> resetPassword(ResetPasswordRequest request) {
        log.info("Processing password reset for email: {}", request.getEmail());
        
        return passwordResetTokenRepository.findByEmailAndCodeAndUsedFalse(
                    request.getEmail(), 
                    request.getCode()
                )
                .switchIfEmpty(Mono.error(new RuntimeException("Invalid or expired reset code")))
                .flatMap(token -> {
                    if (token.getExpiryTime().isBefore(LocalDateTime.now())) {
                        return Mono.error(new RuntimeException("Reset code has expired"));
                    }
                    
                    return userRepository.findByEmail(request.getEmail())
                            .flatMap(user -> {
                                user.setPassword(passwordEncoder.encode(request.getNewPassword()));
                                
                                return userRepository.save(user)
                                        .then(Mono.defer(() -> {
                                            token.setUsed(true);
                                            return passwordResetTokenRepository.save(token);
                                        }))
                                        .thenReturn("Password reset successfully");
                            });
                });
    }
    
    private String generateSixDigitCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }
    
    private String buildPasswordResetEmail(String code) {
        return String.format("""
                Hello,
                
                You requested to reset your password.
                
                Your password reset code is: %s
                
                This code will expire in 15 minutes.
                
                If you did not request this, please ignore this email.
                
                Best Regards,
                Flight Booking Team
                """, code);
    }
}
