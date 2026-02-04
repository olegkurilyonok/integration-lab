package com.olegkurilyonok.practice.kafka.controller;

import com.olegkurilyonok.practice.kafka.dto.MessageRequest;
import com.olegkurilyonok.practice.kafka.dto.MessageResponse;
import com.olegkurilyonok.practice.kafka.service.MessageProducer;
import com.olegkurilyonok.practice.kafka.service.MessageQueue;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/messages")
public class MessageController {
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
}
