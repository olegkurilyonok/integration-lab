package com.olegkurilyonok.practice.kafka.service;

import com.olegkurilyonok.practice.kafka.config.AppKafkaProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class MessageListener {
    private static final Logger logger = LoggerFactory.getLogger(MessageListener.class);
    private final AppKafkaProperties properties;
    private final MessageQueue messageQueue;

    public MessageListener(AppKafkaProperties properties, MessageQueue messageQueue) {
        this.properties = properties;
        this.messageQueue = messageQueue;
    }

    @KafkaListener(topics = "${app.kafka.topic}")
    public void onMessage(String message) {
        logger.info("Received message from {}: {}", properties.topic(), message);
        messageQueue.add(message);
    }
}
