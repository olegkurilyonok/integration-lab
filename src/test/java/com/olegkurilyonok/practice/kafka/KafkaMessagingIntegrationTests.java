package com.olegkurilyonok.practice.kafka;

import com.olegkurilyonok.practice.kafka.dto.MessageRequest;
import com.olegkurilyonok.practice.kafka.service.MessageListener;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.test.context.EmbeddedKafka;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@EmbeddedKafka(partitions = 1, topics = "test-messages")
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
                "app.kafka.topic=test-messages"
        }
)
class KafkaMessagingIntegrationTests {
    @Autowired
    private TestRestTemplate restTemplate;

    @SpyBean
    private MessageListener messageListener;

    @Test
    void postMessage_isConsumedByListener() {
        MessageRequest request = new MessageRequest("Hello from test");

        ResponseEntity<Void> response = restTemplate.postForEntity("/messages", request, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        verify(messageListener, timeout(5_000)).onMessage("Hello from test");
    }
}
