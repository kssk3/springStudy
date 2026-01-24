package com.todoapp.common.exception;

public class InvalidCredentialsException extends BusinessException {
    public InvalidCredentialsException() {
        super(ErrorCode.INVALID_CREDENTIALS);
    }
}
