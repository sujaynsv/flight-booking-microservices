package com.flightapp.bookings.consumer;

import com.flightapp.bookings.dto.EmailNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
// import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;


public class EmailConsumer {
    
    private static final Logger log = LoggerFactory.getLogger(EmailConsumer.class);
    
    // LISTENER DISABLED - Messages will stay in queue
    // @RabbitListener(queues = "${email.queue.name}")
    public void receiveEmail(EmailNotification notification) {
        log.info("Email received: {}", notification);
    }
}
