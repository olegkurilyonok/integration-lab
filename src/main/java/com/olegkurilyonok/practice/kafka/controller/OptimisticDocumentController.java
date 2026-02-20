package com.olegkurilyonok.practice.kafka.controller;

import com.olegkurilyonok.practice.kafka.dto.OptimisticDocumentCreateRequest;
import com.olegkurilyonok.practice.kafka.dto.OptimisticDocumentResponse;
import com.olegkurilyonok.practice.kafka.dto.OptimisticDocumentUpdateRequest;
import com.olegkurilyonok.practice.kafka.entity.OptimisticDocumentEntity;
import com.olegkurilyonok.practice.kafka.service.OptimisticDocumentService;
import java.util.NoSuchElementException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/optimistic/documents")
public class OptimisticDocumentController {
    private final OptimisticDocumentService service;

    public OptimisticDocumentController(OptimisticDocumentService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<OptimisticDocumentResponse> create(
            @RequestBody OptimisticDocumentCreateRequest request) {
        validateText(request.title(), "title");
        validateText(request.content(), "content");
        OptimisticDocumentEntity created = service.create(request.title(), request.content());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toResponse(created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OptimisticDocumentResponse> get(@PathVariable long id) {
        return service
                .findById(id)
                .map(entity -> ResponseEntity.ok(toResponse(entity)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<OptimisticDocumentResponse> update(
            @PathVariable long id,
            @RequestBody OptimisticDocumentUpdateRequest request) {
        validateText(request.title(), "title");
        validateText(request.content(), "content");
        if (request.version() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "version must be non-negative");
        }
        try {
            OptimisticDocumentEntity updated = service.update(
                    id,
                    request.version(),
                    request.title(),
                    request.content());
            return ResponseEntity.ok(toResponse(updated));
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "document not found");
        } catch (OptimisticLockingFailureException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "document version conflict");
        }
    }

    private OptimisticDocumentResponse toResponse(OptimisticDocumentEntity entity) {
        return new OptimisticDocumentResponse(
                entity.getId(),
                entity.getTitle(),
                entity.getContent(),
                entity.getVersion());
    }

    private void validateText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, field + " must not be blank");
        }
    }
}
