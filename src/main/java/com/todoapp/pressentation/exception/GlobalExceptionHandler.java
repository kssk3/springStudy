package com.todoapp.pressentation.exception;

import com.todoapp.common.exception.BusinessException;
import com.todoapp.common.exception.ErrorCode;
import com.todoapp.pressentation.dto.response.ErrorResponse;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String VALIDATION_ERROR_CODE = "C001";
    private static final String VALIDATION_ERROR_MESSAGE = "입력값 검증에 실패했습니다.";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        BindingResult bindingResult = e.getBindingResult();

        if (bindingResult.hasFieldErrors()) {
            bindingResult.getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
            );
        }

        if (bindingResult.hasGlobalErrors()) {
            bindingResult.getGlobalErrors().forEach(error ->
                errors.put(error.getObjectName(), error.getDefaultMessage())
            );
        }

        ErrorCode errorCode = ErrorCode.INVALID_INPUT_VALUE;
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ErrorResponse.of(VALIDATION_ERROR_CODE, VALIDATION_ERROR_MESSAGE, errors));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ErrorResponse.of(errorCode, e.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException e) {
        ErrorCode errorCode = ErrorCode.TODO_NOT_FOUND;
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ErrorResponse.of(errorCode.getCode(), e.getMessage(), null));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ErrorResponse.of(errorCode));
    }
}
