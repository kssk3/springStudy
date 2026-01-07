package test;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TodoResponse {

    private Long id;
    private String title;
    private String description;
    private boolean completed;
    private LocalDateTime createdAt;

    // Entity -> DTO 변경
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
