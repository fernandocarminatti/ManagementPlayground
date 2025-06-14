package com.edu.ManagementPlayground.Dto.CustomValidation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FileTypeValidator.class)
public @interface FileType {
    String message() default "Invalid file type";
    String[] allowed() default {};
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}