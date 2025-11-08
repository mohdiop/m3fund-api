package com.mohdiop.m3fundapi.validator;

import com.mohdiop.m3fundapi.annotation.ValidFileListForPatch;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class ValidFileListForPatchValidator implements ConstraintValidator<ValidFileListForPatch, Set<MultipartFile>> {

    private List<String> allowedTypes;

    @Override
    public void initialize(ValidFileListForPatch constraintAnnotation) {
        this.allowedTypes = Arrays.asList(constraintAnnotation.allowed());
    }

    @Override
    public boolean isValid(Set<MultipartFile> files, ConstraintValidatorContext context) {
        if (files == null || files.isEmpty()) {
            return true;
        }
        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) {
                return false;
            }
            if (file.getContentType() == null || !allowedTypes.contains(file.getContentType())) {
                return false;
            }
        }
        return true;
    }
}
