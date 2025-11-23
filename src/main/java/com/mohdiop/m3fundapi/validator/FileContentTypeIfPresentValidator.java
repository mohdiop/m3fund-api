package com.mohdiop.m3fundapi.validator;

import com.mohdiop.m3fundapi.annotation.FileContentTypeIfPresent;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;

public class FileContentTypeIfPresentValidator implements ConstraintValidator<FileContentTypeIfPresent, MultipartFile> {

    private String[] allowedTypes;

    @Override
    public void initialize(FileContentTypeIfPresent constraintAnnotation) {
        this.allowedTypes = constraintAnnotation.allowed();
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null || file.isEmpty()) return true;
        return Arrays.asList(allowedTypes).contains(file.getContentType());
    }
}

