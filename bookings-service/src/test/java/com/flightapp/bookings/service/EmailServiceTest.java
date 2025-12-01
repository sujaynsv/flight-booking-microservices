package com.flightapp.bookings.service;

import com.flightapp.bookings.dto.EmailNotification;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private EmailService emailService;

    @Test
    void testSendEmail() throws Exception {
        var field = EmailService.class.getDeclaredField("emailQueue");
        field.setAccessible(true);
        field.set(emailService, "test-queue");

        EmailNotification notification = new EmailNotification();
        notification.setTo("user@example.com");
        notification.setSubject("Test");
        notification.setBody("Body");

        StepVerifier.create(emailService.sendEmail(notification))
                .verifyComplete();

        verify(rabbitTemplate, times(1))
                .convertAndSend(eq(""), eq("test-queue"), eq(notification), any(MessagePostProcessor.class));
    }
}
