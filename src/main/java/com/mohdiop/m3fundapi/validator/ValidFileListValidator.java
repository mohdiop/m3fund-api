package com.mohdiop.m3fundapi.validator;

import com.mohdiop.m3fundapi.annotation.ValidFileList;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;
import java.util.*;

public class ValidFileListValidator implements ConstraintValidator<ValidFileList, Set<MultipartFile>> {

    private List<String> allowedTypes;

    @Override
    public void initialize(ValidFileList constraintAnnotation) {
        this.allowedTypes = Arrays.asList(constraintAnnotation.allowed());
    }

    @Override
    public boolean isValid(Set<MultipartFile> files, ConstraintValidatorContext context) {
        if (files == null || files.isEmpty()) return false;
        return files.stream().allMatch(file ->
                file != null &&
                        !file.isEmpty() &&
                        allowedTypes.contains(file.getContentType())
        );
    }
}

