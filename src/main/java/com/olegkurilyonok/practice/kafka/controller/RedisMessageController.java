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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/redis/messages")
public class RedisMessageController {
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
}
