package com.olegkurilyonok.practice.kafka.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

@Service
public class MessageProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String topic;

    public MessageProducer(
            KafkaTemplate<String, String> kafkaTemplate,
            @Value("${app.kafka.topic:practice-messages}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void send(String message) {
        kafkaTemplate.send(topic, message);
    }
}
