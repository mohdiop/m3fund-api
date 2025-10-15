package com.mohdiop.m3fundapi.service;

import com.mohdiop.m3fundapi.entity.enums.FileExtension;
import com.mohdiop.m3fundapi.entity.enums.FileType;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
public class UploadService {

    private final String userHome = System.getProperty("user.home");
    private final String desktopPath = userHome + File.separator + "Desktop";

    public com.mohdiop.m3fundapi.entity.File uploadFile(MultipartFile file, String fileName, FileType fileType, FileExtension fileExtension) {
        boolean isCreatedDirectory;
        switch (fileType) {
            case PICTURE -> {
                File directory = new File(desktopPath + "/m3fund/pictures");
                if (!directory.exists()) {
                    isCreatedDirectory = directory.mkdirs();
                } else {
                    isCreatedDirectory = true;
                }
            }
            case LOGO -> {
                File directory = new File(desktopPath + "/m3fund/logos");
                if (!directory.exists()) {
                    isCreatedDirectory = directory.mkdirs();
                } else {
                    isCreatedDirectory = true;
                }
            }
            case VIDEO -> {
                File directory = new File(desktopPath + "/m3fund/videos");
                if (!directory.exists()) {
                    isCreatedDirectory = directory.mkdirs();
                } else {
                    isCreatedDirectory = true;
                }
            }
            case RESIDENCE -> {
                File directory = new File(desktopPath + "/m3fund/residences");
                if (!directory.exists()) {
                    isCreatedDirectory = directory.mkdirs();
                } else {
                    isCreatedDirectory = true;
                }
            }
            case BIOMETRIC_CARD -> {
                File directory = new File(desktopPath + "/m3fund/biometric-cards");
                if (!directory.exists()) {
                    isCreatedDirectory = directory.mkdirs();
                } else {
                    isCreatedDirectory = true;
                }
            }
            case ASSOCIATION_STATUS -> {
                File directory = new File(desktopPath + "/m3fund/association-status");
                if (!directory.exists()) {
                    isCreatedDirectory = directory.mkdirs();
                } else {
                    isCreatedDirectory = true;
                }
            }
            case RCCM -> {
                File directory = new File(desktopPath + "/m3fund/rccms");
                if (!directory.exists()) {
                    isCreatedDirectory = directory.mkdirs();
                } else {
                    isCreatedDirectory = true;
                }
            }
            case BANK_STATEMENT -> {
                File directory = new File(desktopPath + "/m3fund/bank-statements");
                if (!directory.exists()) {
                    isCreatedDirectory = directory.mkdirs();
                } else {
                    isCreatedDirectory = true;
                }
            }
            case BUSINESS_MODEL -> {
                File directory = new File(desktopPath + "/m3fund/business-models");
                if (!directory.exists()) {
                    isCreatedDirectory = directory.mkdirs();
                } else {
                    isCreatedDirectory = true;
                }
            }
            default -> isCreatedDirectory = false;
        }
        if (isCreatedDirectory) {
            String filePath = getFilePath(fileName, fileType, fileExtension);
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                fos.write(file.getBytes());
                return com.mohdiop.m3fundapi.entity.File.builder()
                        .id(null)
                        .name(fileName)
                        .sizeKo(file.getSize() / 1024D)
                        .type(fileType)
                        .extension(fileExtension)
                        .url(filePath)
                        .build();
            } catch (Exception e) {
                throw new RuntimeException("Un problème est survenu de notre côté, veuillez réessayer plus tard.");
            }
        } else {
            return null;
        }
    }

    private String getFilePath(String fileName, FileType fileType, FileExtension fileExtension) {
        String filePath;
        String dotExtension = getDotExtensionType(fileExtension);
        switch (fileType) {
            case PICTURE ->
                    filePath = desktopPath + File.separator + "m3fund" + File.separator + "pictures" + File.separator + fileName + dotExtension;
            case LOGO -> {
                filePath = desktopPath + File.separator + "m3fund" + File.separator + "logos" + File.separator + fileName + dotExtension;
            }
            case VIDEO -> {
                filePath = desktopPath + File.separator + "m3fund" + File.separator + "videos" + File.separator + fileName + dotExtension;
            }
            case RESIDENCE -> {
                filePath = desktopPath + File.separator + "m3fund" + File.separator + "residences" + File.separator + fileName + dotExtension;
            }
            case BIOMETRIC_CARD -> {
                filePath = desktopPath + File.separator + "m3fund" + File.separator + "biometric-cards" + File.separator + fileName + dotExtension;
            }
            case ASSOCIATION_STATUS -> {
                filePath = desktopPath + File.separator + "m3fund" + File.separator + "association-status" + File.separator + fileName + dotExtension;
            }
            case RCCM -> {
                filePath = desktopPath + File.separator + "m3fund" + File.separator + "rccms" + File.separator + fileName + dotExtension;
            }
            case BANK_STATEMENT -> {
                filePath = desktopPath + File.separator + "m3fund" + File.separator + "bank-statements" + File.separator + fileName + dotExtension;
            }
            case BUSINESS_MODEL -> {
                filePath = desktopPath + File.separator + "m3fund" + File.separator + "business-models" + File.separator + fileName + dotExtension;
            }
            default ->
                    throw new RuntimeException("Un problème est survenu de notre côté, veuillez réessayer plus tard.");
        }
        return filePath;
    }

    private String getDotExtensionType(FileExtension fileExtension) {
        switch (fileExtension) {
            case JPG -> {
                return ".jpg";
            }
            case PNG -> {
                return ".png";
            }
            case PDF -> {
                return ".pdf";
            }
        }
        throw new RuntimeException("Un problème est survenu de notre côté, veuillez réessayer plus tard.");
    }

    public FileExtension getFileExtension(MultipartFile file) {
        Tika tika = new Tika();
        String detectedType;
        try {
            detectedType = tika.detect(file.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException("Un problème est survenu de notre côté, veuillez réessayer plus tard.");
        }
        if ("application/pdf".equals(detectedType)) {
            return FileExtension.PDF;
        } else if ("image/jpeg".equals(detectedType)) {
            return FileExtension.JPG;
        } else {
            return FileExtension.PNG;
        }
    }
}

