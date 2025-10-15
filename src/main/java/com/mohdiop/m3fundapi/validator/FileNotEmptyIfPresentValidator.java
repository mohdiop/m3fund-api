package com.mohdiop.m3fundapi.validator;

import com.mohdiop.m3fundapi.annotation.FileNotEmptyIfPresent;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

public class FileNotEmptyIfPresentValidator implements ConstraintValidator<FileNotEmptyIfPresent, MultipartFile> {
    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        return file == null || !file.isEmpty();
    }
}

