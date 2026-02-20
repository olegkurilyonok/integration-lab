package com.olegkurilyonok.practice.kafka.service;

import com.olegkurilyonok.practice.kafka.entity.OptimisticDocumentEntity;
import com.olegkurilyonok.practice.kafka.repository.OptimisticDocumentRepository;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OptimisticDocumentService {
    private final OptimisticDocumentRepository repository;

    public OptimisticDocumentService(OptimisticDocumentRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public OptimisticDocumentEntity create(String title, String content) {
        return repository.save(new OptimisticDocumentEntity(title, content));
    }

    @Transactional(readOnly = true)
    public Optional<OptimisticDocumentEntity> findById(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public OptimisticDocumentEntity update(Long id, long expectedVersion, String title, String content) {
        OptimisticDocumentEntity entity = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Document not found"));
        if (!Objects.equals(entity.getVersion(), expectedVersion)) {
            throw new OptimisticLockingFailureException("Document version mismatch");
        }
        entity.update(title, content);
        return repository.save(entity);
    }
}
