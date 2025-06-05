package org.example.kafka;

import lombok.RequiredArgsConstructor;
import org.example.event.UserRegisteredEvent;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserRegistrationProducer {

    @Qualifier("userEventKafkaTemplate")
    private final KafkaTemplate<String, UserRegisteredEvent> kafkaTemplate;

    public void send(UserRegisteredEvent event) {
        kafkaTemplate.send("user-registered", event);
    }
}
