package com.olegkurilyonok.practice.kafka.dto;

public record MemoryStatusResponse(int totalAllocatedMb, int maxMb) {
}
