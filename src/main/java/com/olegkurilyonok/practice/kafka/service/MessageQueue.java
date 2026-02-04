package com.olegkurilyonok.practice.kafka.service;

import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
public class MessageQueue {
    private final Queue<String> queue = new ConcurrentLinkedQueue<>();

    public void add(String message) {
        queue.add(message);
    }

    public Optional<String> poll() {
        return Optional.ofNullable(queue.poll());
    }
}
