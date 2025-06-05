package org.example.kafka;

public interface KafkaMessageProducer {
    <T> void send(String topic, T payload);
}
