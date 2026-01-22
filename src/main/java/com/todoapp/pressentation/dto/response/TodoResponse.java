package com.todoapp.pressentation.dto.response;

import com.todoapp.domain.Todo;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TodoResponse {

    private Long id;
    private String title;
    private String description;
    private boolean completed;
    private Instant createdAt;

    public static TodoResponse from(Todo todo) {
        return new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getDescription(),
                todo.isCompleted(),
                todo.getCreatedAt()
        );
    }
}
