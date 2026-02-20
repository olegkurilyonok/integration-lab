package com.olegkurilyonok.practice.kafka.dto;

public record OptimisticDocumentUpdateRequest(String title, String content, long version) {
}
