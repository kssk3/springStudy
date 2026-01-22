package com.todoapp.pressentation.exception;

import com.todoapp.common.exception.DuplicateEmailException;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<Map<String, String>> handleDuplicateEmail(DuplicateEmailException e) {
        Map<String, String> errors = Map.of(
                "error", "Duplicate email address",
                "message", e.getMessage());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(errors);
    }
}
