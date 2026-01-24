package com.todoapp.implement.validator;

import com.todoapp.pressentation.dto.request.SignUpRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, SignUpRequest> {

    @Override
    public boolean isValid(SignUpRequest request, ConstraintValidatorContext context) {
        if (request.getPassword() == null || request.getPasswordConfirm() == null) {
            return true;
        }

        return request.getPassword().equals(request.getPasswordConfirm());
    }
}
