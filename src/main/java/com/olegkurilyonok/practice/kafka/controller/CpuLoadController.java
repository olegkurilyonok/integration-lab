package com.olegkurilyonok.practice.kafka.controller;

import com.olegkurilyonok.practice.kafka.dto.CpuLoadResponse;
import com.olegkurilyonok.practice.kafka.service.CpuLoadService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test/cpu")
public class CpuLoadController {
    private final CpuLoadService cpuLoadService;

    public CpuLoadController(CpuLoadService cpuLoadService) {
        this.cpuLoadService = cpuLoadService;
    }

    @PostMapping("/burn")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CpuLoadResponse burn(
            @RequestParam int seconds,
            @RequestParam(defaultValue = "1") int threads) {
        return cpuLoadService.burn(seconds, threads);
    }
}
