package com.olegkurilyonok.practice.kafka.service;

import com.olegkurilyonok.practice.kafka.dto.CpuLoadResponse;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CpuLoadService {
    private static final long NANOS_PER_SECOND = TimeUnit.SECONDS.toNanos(1);
    private static volatile long blackhole;
    private final int maxSeconds;
    private final int maxThreads;

    public CpuLoadService(
            @Value("${app.cpu.max-seconds:60}") int maxSeconds,
            @Value("${app.cpu.max-threads:8}") int maxThreads) {
        if (maxSeconds <= 0) {
            throw new IllegalArgumentException("app.cpu.max-seconds must be positive");
        }
        if (maxThreads <= 0) {
            throw new IllegalArgumentException("app.cpu.max-threads must be positive");
        }
        this.maxSeconds = maxSeconds;
        this.maxThreads = maxThreads;
    }

    public CpuLoadResponse burn(int seconds, int threads) {
        if (seconds <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "seconds must be positive");
        }
        if (threads <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "threads must be positive");
        }
        if (seconds > maxSeconds) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "seconds exceeds max-seconds");
        }
        if (threads > maxThreads) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "threads exceeds max-threads");
        }
        long durationNanos = seconds * NANOS_PER_SECOND;
        CountDownLatch latch = new CountDownLatch(threads);
        for (int i = 0; i < threads; i++) {
            Thread worker = new Thread(() -> runBurn(durationNanos, latch), "cpu-burn-" + i);
            worker.setDaemon(true);
            worker.start();
        }
        try {
            latch.await();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "cpu burn interrupted");
        }
        return new CpuLoadResponse(seconds, threads);
    }

    private void runBurn(long durationNanos, CountDownLatch latch) {
        long end = System.nanoTime() + durationNanos;
        long acc = 0;
        while (System.nanoTime() < end) {
            acc += 31;
            acc ^= (acc << 1);
            if ((acc & 0xFFF) == 0) {
                acc += System.nanoTime();
            }
        }
        blackhole = acc;
        latch.countDown();
    }
}
