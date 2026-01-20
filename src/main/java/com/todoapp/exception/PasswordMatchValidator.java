package com.todoapp.exception;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswrodMatchValidator implements ConstraintValidator {
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return false;
    }
}
