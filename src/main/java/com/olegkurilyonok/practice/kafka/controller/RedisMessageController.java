package com.olegkurilyonok.practice.kafka.controller;

import com.olegkurilyonok.practice.kafka.dto.RedisMessageRequest;
import com.olegkurilyonok.practice.kafka.dto.RedisMessageResponse;
import com.olegkurilyonok.practice.kafka.service.RedisMessageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/redis/messages")
public class RedisMessageController {
    private static final int DEFAULT_GENERATION_THOUSANDS = 20;
    private static final int MESSAGES_PER_THOUSAND = 1_000;
    private final RedisMessageService redisMessageService;

    public RedisMessageController(RedisMessageService redisMessageService) {
        this.redisMessageService = redisMessageService;
    }

    @PostMapping
    public ResponseEntity<RedisMessageResponse> save(@RequestBody RedisMessageRequest request) {
        redisMessageService.save(request.key(), request.message());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new RedisMessageResponse(request.key(), request.message()));
    }

    @GetMapping("/{key}")
    public ResponseEntity<RedisMessageResponse> get(@PathVariable String key) {
        return redisMessageService
                .get(key)
                .map(message -> ResponseEntity.ok(new RedisMessageResponse(key, message)))
                .orElseGet(() -> ResponseEntity.notFound().build());
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
        redisMessageService.saveGeneratedMessages((int) totalMessages);
    }
}
