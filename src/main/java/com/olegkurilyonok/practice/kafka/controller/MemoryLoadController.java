package com.olegkurilyonok.practice.kafka.controller;

import com.olegkurilyonok.practice.kafka.dto.MemoryStatusResponse;
import com.olegkurilyonok.practice.kafka.service.MemoryLoadService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test/memory")
public class MemoryLoadController {
    private final MemoryLoadService memoryLoadService;

    public MemoryLoadController(MemoryLoadService memoryLoadService) {
        this.memoryLoadService = memoryLoadService;
    }

    @PostMapping("/allocate")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public MemoryStatusResponse allocate(@RequestParam int mb) {
        return memoryLoadService.allocate(mb);
    }

    @PostMapping("/clear")
    public ResponseEntity<MemoryStatusResponse> clear() {
        return ResponseEntity.ok(memoryLoadService.clear());
    }

    @GetMapping("/status")
    public ResponseEntity<MemoryStatusResponse> status() {
        return ResponseEntity.ok(memoryLoadService.status());
    }
}
