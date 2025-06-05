package org.example.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaMessageProducerImpl implements KafkaMessageProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public <T> void send(String topic, T payload) {
        kafkaTemplate.send(topic, payload);
    }
}
