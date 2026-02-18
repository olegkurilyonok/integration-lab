package com.olegkurilyonok.practice.kafka.service;

import com.olegkurilyonok.practice.kafka.dto.MemoryStatusResponse;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MemoryLoadService {
    private static final int BYTES_PER_MB = 1024 * 1024;
    private final int maxMb;
    private final List<byte[]> allocations = new ArrayList<>();
    private final Object lock = new Object();

    public MemoryLoadService(@Value("${app.memory.max-mb:1024}") int maxMb) {
        if (maxMb <= 0) {
            throw new IllegalArgumentException("app.memory.max-mb must be positive");
        }
        this.maxMb = maxMb;
    }

    public MemoryStatusResponse allocate(int mb) {
        if (mb <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "mb must be positive");
        }
        synchronized (lock) {
            long total = (long) allocations.size() + mb;
            if (total > maxMb) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "requested memory exceeds max-mb");
            }
            for (int i = 0; i < mb; i++) {
                allocations.add(new byte[BYTES_PER_MB]);
            }
            return currentStatus();
        }
    }

    public MemoryStatusResponse clear() {
        synchronized (lock) {
            allocations.clear();
            return currentStatus();
        }
    }

    public MemoryStatusResponse status() {
        synchronized (lock) {
            return currentStatus();
        }
    }

    private MemoryStatusResponse currentStatus() {
        return new MemoryStatusResponse(allocations.size(), maxMb);
    }
}
