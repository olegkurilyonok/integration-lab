package com.olegkurilyonok.practice.kafka.service;

import com.olegkurilyonok.practice.kafka.entity.MessageEntity;
import com.olegkurilyonok.practice.kafka.repository.MessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class MessagePersistenceService {
    private static final Logger logger = LoggerFactory.getLogger(MessagePersistenceService.class);

    private final MessageQueue messageQueue;
    private final MessageRepository messageRepository;

    public MessagePersistenceService(MessageQueue messageQueue, MessageRepository messageRepository) {
        this.messageQueue = messageQueue;
        this.messageRepository = messageRepository;
    }

    @Scheduled(fixedDelayString = "${app.queue.drain.fixed-delay-ms:1000}")
    @Transactional
    public void drainQueueToDatabase() {
        Optional<String> message = messageQueue.poll();
        while (message.isPresent()) {
            MessageEntity saved = messageRepository.save(new MessageEntity(message.get()));
            logger.info("Saved message id={} content={}", saved.getId(), saved.getContent());
            message = messageQueue.poll();
        }
    }
}
