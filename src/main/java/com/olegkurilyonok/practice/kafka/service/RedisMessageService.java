package com.olegkurilyonok.practice.kafka.service;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisMessageService {
    private final StringRedisTemplate redisTemplate;
    private final Duration messageTtl;
    private final String generatedKeyPrefix;
    private final String generatedMessagePrefix;

    public RedisMessageService(
            StringRedisTemplate redisTemplate,
            @Value("${app.redis.message-ttl:30s}") Duration messageTtl,
            @Value("${app.redis.generated-key-prefix:generated}") String generatedKeyPrefix,
            @Value("${app.redis.generated-message-prefix:Generated redis message}") String generatedMessagePrefix) {
        this.redisTemplate = redisTemplate;
        this.messageTtl = messageTtl;
        this.generatedKeyPrefix = generatedKeyPrefix;
        this.generatedMessagePrefix = generatedMessagePrefix;
    }

    public void save(String key, String message) {
        redisTemplate.opsForValue().set(key, message, messageTtl);
    }

    public Optional<String> get(String key) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(key));
    }

    public void saveGeneratedMessages(int totalMessages) {
        for (int i = 1; i <= totalMessages; i++) {
            String id = UUID.randomUUID().toString();
            String key = buildGeneratedKey(id);
            String message = generatedMessagePrefix + " " + i
                    + " | uuid=" + id
                    + " | ts=" + Instant.now();
            save(key, message);
        }
    }

    private String buildGeneratedKey(String id) {
        if (generatedKeyPrefix == null || generatedKeyPrefix.isBlank()) {
            return "generated:" + id;
        }
        return generatedKeyPrefix.endsWith(":")
                ? generatedKeyPrefix + id
                : generatedKeyPrefix + ":" + id;
    }
}
