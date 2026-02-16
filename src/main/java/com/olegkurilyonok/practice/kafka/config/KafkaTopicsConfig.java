package com.olegkurilyonok.practice.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicsConfig {
    @Bean
    public NewTopic messagesTopic(@Value("${app.kafka.topic:practice-messages}") String topic) {
        return TopicBuilder.name(topic).build();
    }
}
