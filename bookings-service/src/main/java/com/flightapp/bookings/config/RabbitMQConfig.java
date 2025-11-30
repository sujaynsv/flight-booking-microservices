package com.flightapp.bookings.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfig {
    
    private static final Logger log = LoggerFactory.getLogger(RabbitMQConfig.class);
    
    @Value("${email.queue.name}")
    private String emailQueue;
    
    @PostConstruct
    public void init() {
        log.info("=== RabbitMQ Configuration Initialized ===");
    }
    
    @Bean
    public Queue emailQueue() {
        log.info("=== Creating Queue Bean: {} ===", emailQueue);
        
        // Create durable queue with no auto-delete
        Map<String, Object> args = new HashMap<>();
        Queue queue = new Queue(emailQueue, true, false, false, args);
        
        log.info("=== Queue Bean Created: {} ===", emailQueue);
        return queue;
    }
    
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    
    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin admin = new RabbitAdmin(connectionFactory);
        admin.setAutoStartup(true);
        
        // Explicitly declare the queue
        admin.declareQueue(emailQueue());
        
        log.info("=== RabbitAdmin created and queue declared ===");
        return admin;
    }
    
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        
        // Set default routing key
        template.setRoutingKey(emailQueue);
        
        log.info("=== RabbitTemplate configured ===");
        return template;
    }
}
