package com.olegkurilyonok.practice.kafka.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class MessageListener {
    private static final Logger logger = LoggerFactory.getLogger(MessageListener.class);
    private final String topic;
    private final MessageQueue messageQueue;

    public MessageListener(
            @Value("${app.kafka.topic:practice-messages}") String topic,
            MessageQueue messageQueue) {
        this.topic = topic;
        this.messageQueue = messageQueue;
    }

    @KafkaListener(topics = "${app.kafka.topic}")
    public void onMessage(String message) {
        logger.info("Received message from {}: {}", topic, message);
        messageQueue.add(message);
    }
}
