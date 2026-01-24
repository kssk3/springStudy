package com.todoapp.pressentation.dto.response;

import com.todoapp.common.exception.ErrorCode;
import java.time.Instant;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {

    private String code;
    private String message;
    private Instant timestamp;
    private Map<String, String> errors;

    public static ErrorResponse of(ErrorCode errorCode) {
        return new ErrorResponse(
                errorCode.getCode(),
                errorCode.getMessage(),
                Instant.now(),
                null
        );
    }

    public static ErrorResponse of(ErrorCode errorCode, String message) {
        return new ErrorResponse(
                errorCode.getCode(),
                message,
                Instant.now(),
                null
        );
    }

    public static ErrorResponse of(String code, String message, Map<String, String> errors) {
        return new ErrorResponse(
                code,
                message,
                Instant.now(),
                errors
        );
    }
}
