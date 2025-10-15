package com.mohdiop.m3fundapi.validator;

import com.mohdiop.m3fundapi.annotation.FileNotEmpty;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

public class FileNotEmptyValidator implements ConstraintValidator<FileNotEmpty, MultipartFile> {

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        // Valide uniquement si le fichier est non null ET non vide
        return file != null && !file.isEmpty();
    }
}

