package com.flightapp.email.service;

import com.flightapp.email.dto.EmailNotification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailConsumerService {
    
    private static final Logger log = LoggerFactory.getLogger(EmailConsumerService.class);
    
    private final JavaMailSender mailSender;
    
    public EmailConsumerService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
    
    @RabbitListener(queues = "${email.queue.name}")
    public void consumeEmailMessage(EmailNotification notification) {
        try {
            log.info("Received email message from queue: {}", notification);
            log.info("Sending email to: {}", notification.getTo());
            
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(notification.getTo());
            message.setSubject(notification.getSubject());
            message.setText(notification.getBody());
            message.setFrom("noreply@flightbooking.com");
            
            mailSender.send(message);
            
            log.info("Email sent successfully to: {}", notification.getTo());
            
        } catch (Exception e) {
            log.error("Failed to send email to: {}", notification.getTo());
            log.error("Error: {}", e.getMessage());
            log.error("Stack trace:", e);
        }
    }
}
