package com.flightapp.email.listener;

import com.flightapp.email.dto.EmailNotification;
import com.flightapp.email.service.EmailSenderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class EmailListener {
    
    private static final Logger log = LoggerFactory.getLogger(EmailListener.class);
    
    private final EmailSenderService emailSenderService;
    
    public EmailListener(EmailSenderService emailSenderService) {
        this.emailSenderService = emailSenderService;
    }
    
    @RabbitListener(queues = "${email.queue.name}")
    public void handleEmailNotification(EmailNotification notification) {
        log.info("===== RECEIVED EMAIL FROM QUEUE =====");
        log.info("To: {}", notification.getTo());
        log.info("Subject: {}", notification.getSubject());
        
        try {
            emailSenderService.sendEmail(
                notification.getTo(),
                notification.getSubject(),
                notification.getBody()
            );
            
            log.info("===== EMAIL SENT SUCCESSFULLY =====");
            
        } catch (Exception e) {
            log.error("===== FAILED TO SEND EMAIL =====");
            log.error("Error: {}", e.getMessage(), e);
        }
    }
}
