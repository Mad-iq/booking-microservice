package com.booking.publisher;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.booking.config.RabbitMQConfig;
import com.booking.events.EmailPayload;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EmailPublisher {
    private final RabbitTemplate rabbitTemplate;

    public void publishEmailEvent(EmailPayload payload) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.EMAIL_QUEUE, payload);
        System.out.println("Published email event to queue: " + payload);
    }
}
