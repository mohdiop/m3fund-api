package com.mohdiop.m3fundapi.service;

import com.mohdiop.m3fundapi.dto.response.SystemResponse;
import com.mohdiop.m3fundapi.entity.System;
import com.mohdiop.m3fundapi.repository.SystemRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class SystemService {

    private final SystemRepository systemRepository;

    public SystemService(SystemRepository systemRepository) {
        this.systemRepository = systemRepository;
    }

    public void createSystem(
            String systemName,
            String systemVersion,
            double initialFund
    ) {
        if (systemRepository.findById(1L).isPresent()) {
            var system = systemRepository.findById(1L).get();
            if (!Objects.equals(system.getVersion(), systemVersion)) {
                system.setVersion(systemVersion);
                systemRepository.save(system);
            }
            return;
        }
        systemRepository.save(
                System.builder()
                        .name(systemName)
                        .version(systemVersion)
                        .fund(initialFund)
                        .createdAt(LocalDateTime.now())
                        .build()
        );
    }

    public SystemResponse getSystemInfo() {
        return systemRepository.findAll().getFirst().toResponse();
    }
}
