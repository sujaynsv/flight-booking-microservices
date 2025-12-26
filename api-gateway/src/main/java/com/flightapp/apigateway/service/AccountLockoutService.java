package com.flightapp.apigateway.service;

import com.flightapp.apigateway.model.User;
import com.flightapp.apigateway.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
public class AccountLockoutService {
    
    private static final Logger log = LoggerFactory.getLogger(AccountLockoutService.class);
    
    private static final int MAX_FAILED_ATTEMPTS = 4;
    private static final int LOCKOUT_DURATION_MINUTES = 10;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    @Value("${email.queue.name}")
    private String emailQueueName;
    
    public Mono<Boolean> isAccountLocked(String email) {
        return userRepository.findByEmail(email)
            .map(user -> {
                if (user.getAccountLocked() != null && user.getAccountLocked()) {
                    // Check if lockout period has expired
                    if (user.getAccountLockedUntil() != null && 
                        LocalDateTime.now().isAfter(user.getAccountLockedUntil())) {
                        // Unlock account
                        log.info("Lockout period expired for user: {}", email);
                        return false;
                    }
                    log.warn("Account is locked for user: {}", email);
                    return true;
                }
                return false;
            })
            .defaultIfEmpty(false);
    }
    
    public Mono<User> recordFailedLoginAttempt(String email) {
        return userRepository.findByEmail(email)
            .flatMap(user -> {
                Integer attempts = user.getFailedLoginAttempts() != null ? 
                    user.getFailedLoginAttempts() : 0;
                attempts++;
                
                log.warn("Failed login attempt {} for user: {}", attempts, email);
                
                user.setFailedLoginAttempts(attempts);
                
                if (attempts >= MAX_FAILED_ATTEMPTS) {
                    // Lock the account
                    LocalDateTime lockoutUntil = LocalDateTime.now().plusMinutes(LOCKOUT_DURATION_MINUTES);
                    user.setAccountLocked(true);
                    user.setAccountLockedUntil(lockoutUntil);
                    
                    log.error("Account locked for user: {} until {}", email, lockoutUntil);
                    
                    // Send email notification
                    sendAccountLockedEmail(email, lockoutUntil);
                }
                
                return userRepository.save(user);
            });
    }
    
    public Mono<User> resetFailedLoginAttempts(String email) {
        return userRepository.findByEmail(email)
            .flatMap(user -> {
                user.setFailedLoginAttempts(0);
                user.setAccountLocked(false);
                user.setAccountLockedUntil(null);
                
                log.info("Reset failed login attempts for user: {}", email);
                
                return userRepository.save(user);
            });
    }
    
    public Mono<LocalDateTime> getRemainingLockoutTime(String email) {
        return userRepository.findByEmail(email)
            .map(user -> user.getAccountLockedUntil())
            .defaultIfEmpty(null);
    }
    
    private void sendAccountLockedEmail(String email, LocalDateTime lockoutUntil) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedTime = lockoutUntil.format(formatter);
            
            Map<String, String> emailMessage = new HashMap<>();
            emailMessage.put("to", email);
            emailMessage.put("subject", "Account Locked - Security Alert");
            emailMessage.put("body", String.format(
                "Dear User,\n\n" +
                "Your account has been temporarily locked due to multiple failed login attempts.\n\n" +
                "Your account will be automatically unlocked at: %s\n" +
                "Lockout duration: %d minutes\n\n" +
                "If you did not attempt to login, please contact support immediately.\n\n" +
                "For security reasons, please ensure you are using the correct password.\n\n" +
                "Best regards,\n" +
                "Flight Booking Team",
                formattedTime,
                LOCKOUT_DURATION_MINUTES
            ));
            
            rabbitTemplate.convertAndSend(emailQueueName, emailMessage);
            log.info("Account locked email sent to: {}", email);
            
        } catch (Exception e) {
            log.error("Failed to send account locked email to {}: {}", email, e.getMessage());
        }
    }
}
