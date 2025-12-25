package com.flightapp.apigateway.repository;

import com.flightapp.apigateway.model.PasswordResetToken;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface PasswordResetTokenRepository extends ReactiveMongoRepository<PasswordResetToken, String> {
    
    Mono<PasswordResetToken> findByEmailAndCodeAndUsedFalse(String email, String code);
    
    Mono<Void> deleteByEmail(String email);
}
