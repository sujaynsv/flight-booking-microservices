package com.flightapp.bookings.service;

import com.flightapp.bookings.dto.EmailNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
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
                log.info("=== Sending Email to Queue ===");
                log.info("Queue name: {}", emailQueue);
                log.info("Notification: {}", notification);
                
                MessagePostProcessor messagePostProcessor = message -> {
                    MessageProperties props = message.getMessageProperties();
                    props.setDeliveryMode(MessageProperties.DEFAULT_DELIVERY_MODE);
                    props.setPriority(0);
                    return message;
                };
                
                rabbitTemplate.convertAndSend("", emailQueue, notification, messagePostProcessor);
                
                log.info("=== Email sent successfully! ===");
                
            } catch (Exception e) {
                log.error("=== Failed to send email! ===");
                log.error("Error: {}", e.getMessage());
                log.error("Stack trace:", e);
            }
        });
    }
}