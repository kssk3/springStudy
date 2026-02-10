package com.todoapp.implement.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NullOrNotBlankValidator implements ConstraintValidator<NullOrNotBlank, String> {

    @Override
    public boolean isValid(String valid, ConstraintValidatorContext constraintValidatorContext) {
        if(valid==null) return true;

        return !valid.isBlank();
    }
}
