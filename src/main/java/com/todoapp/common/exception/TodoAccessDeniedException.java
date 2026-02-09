package com.todoapp.common.exception;

public class TodoAccessDeniedException extends BusinessException {

    public TodoAccessDeniedException() {
        super(ErrorCode.FORBIDDEN_TODO_ACCESS);
    }
}
