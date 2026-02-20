package com.olegkurilyonok.practice.kafka.dto;

public record OptimisticDocumentResponse(Long id, String title, String content, long version) {
}
