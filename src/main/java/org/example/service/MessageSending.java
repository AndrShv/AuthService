package org.example.service;

import lombok.Getter;
import lombok.Setter;
import org.example.event.UserRegisteredEvent;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.lang.model.element.NestingKind;

@Service
@Getter
@Setter
public class MessageSending {

    @Autowired
    private TopicExchange userExchange;


    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendUserRegisteredEvent(UserRegisteredEvent event) {
        String routingKey = "user.registered";
        System.out.println(event.getId());
        System.out.println(event.getEmail());
        System.out.println(event.getUsername());
        rabbitTemplate.convertAndSend(userExchange.getName(), routingKey, event);
    }

}
