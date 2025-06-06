package org.example.service;

import lombok.Getter;
import lombok.Setter;
import org.example.event.UserRegisteredEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Getter
@Setter
public class MessageSending {
    @Value("${queue.name:SendFromAuth}")
    private String queueName;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendUserRegisteredEvent(UserRegisteredEvent event) {
        rabbitTemplate.convertAndSend(queueName, event);
    }

}
