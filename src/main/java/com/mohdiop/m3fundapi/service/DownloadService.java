package com.mohdiop.m3fundapi.service;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class DownloadService {

    public ResponseEntity<Resource> downloadByPath(String absolutePath) {
        File file = new File(absolutePath);

        if (!file.exists()) {
            throw new RuntimeException("Fichier introuvable.");
        }

        Resource resource = new FileSystemResource(file);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                .contentLength(file.length())
                .body(resource);
    }
}
