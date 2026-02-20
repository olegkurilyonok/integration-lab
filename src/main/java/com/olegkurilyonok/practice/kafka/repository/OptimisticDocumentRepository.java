package com.olegkurilyonok.practice.kafka.repository;

import com.olegkurilyonok.practice.kafka.entity.OptimisticDocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OptimisticDocumentRepository extends JpaRepository<OptimisticDocumentEntity, Long> {
}
