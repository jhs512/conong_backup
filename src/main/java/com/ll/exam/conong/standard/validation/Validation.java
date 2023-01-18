package com.ll.exam.conong.standard.validation;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class Validation {
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @Constraint(validatedBy = NotEmptyMultipartValidator.class)
    public @interface NotEmptyMultipart {
        String message() default "파일이 비어있습니다.";

        Class[] groups() default {};

        Class[] payload() default {};
    }

    public static class NotEmptyMultipartValidator implements ConstraintValidator<NotEmptyMultipart, MultipartFile> {
        @Override
        public boolean isValid(MultipartFile multipartFile, ConstraintValidatorContext context) {
            return multipartFile.isEmpty() == false;
        }
    }
}