package com.olegkurilyonok.practice.kafka.controller;

import com.olegkurilyonok.practice.kafka.dto.MessageRequest;
import com.olegkurilyonok.practice.kafka.dto.MessageResponse;
import com.olegkurilyonok.practice.kafka.service.MessageProducer;
import com.olegkurilyonok.practice.kafka.service.MessageQueue;
import java.time.Instant;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/messages")
public class MessageController {
    private static final int DEFAULT_GENERATION_THOUSANDS = 20;
    private static final int MESSAGES_PER_THOUSAND = 1000;
    private final MessageProducer messageProducer;
    private final MessageQueue messageQueue;

    public MessageController(MessageProducer messageProducer, MessageQueue messageQueue) {
        this.messageProducer = messageProducer;
        this.messageQueue = messageQueue;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void sendMessage(@RequestBody MessageRequest request) {
        messageProducer.send(request.message());
    }

    @GetMapping
    public ResponseEntity<MessageResponse> readMessage() {
        return messageQueue
                .poll()
                .map(message -> ResponseEntity.ok(new MessageResponse(message)))
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PostMapping("/send-multiple")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void sendMessages(@RequestBody(required = false) String[] messages) {
        if (messages == null || messages.length == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "messages array must not be empty");
        }
        for (String message : messages) {
            if (message == null || message.isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "message must not be blank");
            }
            messageProducer.send(message);
        }
    }

    @PostMapping("/generate")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void generateMessages(@RequestParam(name = "thousands") int thousands) {
        generateMessagesInternal(thousands);
    }

    @PostMapping("/generate-20k")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void generate20kMessages() {
        generateMessagesInternal(DEFAULT_GENERATION_THOUSANDS);
    }

    private void generateMessagesInternal(int thousands) {
        if (thousands <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "thousands must be positive");
        }
        long totalMessages = (long) thousands * MESSAGES_PER_THOUSAND;
        if (totalMessages > Integer.MAX_VALUE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "requested message count is too large");
        }
        for (int i = 1; i <= totalMessages; i++) {
            String message = "Generated message " + i
                    + " | uuid=" + UUID.randomUUID()
                    + " | ts=" + Instant.now();
            messageProducer.send(message);
        }
    }

}
