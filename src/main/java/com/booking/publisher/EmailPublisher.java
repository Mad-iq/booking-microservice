package com.booking.publisher;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.booking.events.EmailPayload;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishEmailEvent(EmailPayload payload) {
        rabbitTemplate.convertAndSend(
                com.booking.config.RabbitConfig.EXCHANGE,
                com.booking.config.RabbitConfig.ROUTING_KEY,
                payload
        );
        System.out.println("Published email event to queue: " + payload);
    }
}
