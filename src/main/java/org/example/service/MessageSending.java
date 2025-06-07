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
        System.out.println(event.getId());
        System.out.println(event.getEmail());
        System.out.println(event.getUsername());
        rabbitTemplate.convertAndSend(queueName, event);
    }

}
