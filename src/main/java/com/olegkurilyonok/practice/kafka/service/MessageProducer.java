package com.olegkurilyonok.practice.kafka.service;

import com.olegkurilyonok.practice.kafka.config.AppKafkaProperties;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class MessageProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final AppKafkaProperties properties;

    public MessageProducer(KafkaTemplate<String, String> kafkaTemplate, AppKafkaProperties properties) {
        this.kafkaTemplate = kafkaTemplate;
        this.properties = properties;
    }

    public void send(String message) {
        String topic = properties.topic();
        kafkaTemplate.send(topic, message);
    }
}
