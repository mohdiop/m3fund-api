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

        String contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        try {
            contentType = java.nio.file.Files.probeContentType(file.toPath());
        } catch (Exception ignored) {}

        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .contentLength(file.length())
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getName() + "\"")
                .body(resource);
    }

}
