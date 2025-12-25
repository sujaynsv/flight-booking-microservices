package com.flightapp.apigateway.service;

import com.flightapp.apigateway.dto.EmailNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class EmailService {
    
    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    
    private final RabbitTemplate rabbitTemplate;
    
    @Value("${email.queue.name}")
    private String emailQueue;
    
    public EmailService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }
    
    public Mono<Void> sendEmail(EmailNotification notification) {
        return Mono.fromRunnable(() -> {
            try {
                log.info("Sending email to queue: {}", emailQueue);
                log.info("Recipient: {}", notification.getTo());
                
                rabbitTemplate.convertAndSend("", emailQueue, notification);
                
                log.info("Email sent successfully to queue");
                
            } catch (Exception e) {
                log.error("Failed to send email: {}", e.getMessage(), e);
            }
        });
    }
}
