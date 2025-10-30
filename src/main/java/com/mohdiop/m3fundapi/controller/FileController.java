package com.mohdiop.m3fundapi.controller;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/files")
public class FileController {

    private final String userHome = System.getProperty("user.home");
    private final String desktopPath = userHome + File.separator + "Desktop";

    @GetMapping("/{fileType}/{fileName}")
    public ResponseEntity<Resource> getFile(@PathVariable String fileType, @PathVariable String fileName) {
        try {
            // Construire le chemin du fichier
            String filePath = desktopPath + File.separator + "m3fund" + File.separator + fileType + File.separator + fileName;
            Path path = Paths.get(filePath);
            Resource resource = new FileSystemResource(path);

            if (resource.exists() && resource.isReadable()) {
                // Déterminer le type MIME
                String contentType = Files.probeContentType(path);
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
