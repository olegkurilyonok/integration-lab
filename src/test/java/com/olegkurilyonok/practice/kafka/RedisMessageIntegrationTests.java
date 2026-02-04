package com.olegkurilyonok.practice.kafka;

import com.olegkurilyonok.practice.kafka.dto.RedisMessageRequest;
import com.olegkurilyonok.practice.kafka.dto.RedisMessageResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

// Optional Testcontainers: uncomment this annotation and the block at the end of the class.
// @org.testcontainers.junit.jupiter.Testcontainers
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.kafka.listener.auto-startup=false",
                "spring.data.redis.host=localhost",
                "spring.data.redis.port=6379"
        }
)
class RedisMessageIntegrationTests {
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void saveAndReadMessage() {
        RedisMessageRequest request = new RedisMessageRequest("test-key", "Hello from Redis test");

        ResponseEntity<RedisMessageResponse> postResponse =
                restTemplate.postForEntity("/redis/messages", request, RedisMessageResponse.class);

        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(postResponse.getBody()).isNotNull();
        assertThat(postResponse.getBody().key()).isEqualTo("test-key");
        assertThat(postResponse.getBody().message()).isEqualTo("Hello from Redis test");

        ResponseEntity<RedisMessageResponse> getResponse =
                restTemplate.getForEntity("/redis/messages/test-key", RedisMessageResponse.class);

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).isNotNull();
        assertThat(getResponse.getBody().message()).isEqualTo("Hello from Redis test");
    }

    @Test
    void missingKeyReturnsNotFound() {
        ResponseEntity<RedisMessageResponse> response =
                restTemplate.getForEntity("/redis/messages/missing-key", RedisMessageResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    /*
     * Optional Testcontainers setup (requires Docker). Uncomment to use containerized Redis:
     *
     * @org.testcontainers.junit.jupiter.Container
     * static org.testcontainers.containers.GenericContainer<?> redis =
     *         new org.testcontainers.containers.GenericContainer<>("redis:7.2-alpine")
     *                 .withExposedPorts(6379);
     *
     * @org.springframework.test.context.DynamicPropertySource
     * static void redisProperties(org.springframework.test.context.DynamicPropertyRegistry registry) {
     *     registry.add("spring.data.redis.host", redis::getHost);
     *     registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
     * }
     */
}
