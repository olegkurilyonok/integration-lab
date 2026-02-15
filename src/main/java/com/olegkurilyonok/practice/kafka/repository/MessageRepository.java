package com.olegkurilyonok.practice.kafka.repository;

import com.olegkurilyonok.practice.kafka.entity.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<MessageEntity, Long> {
}
