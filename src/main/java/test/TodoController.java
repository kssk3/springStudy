package test;

import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/todos")
@AllArgsConstructor
public class TodoController {

    private final TodoService todoService;

    // 생성 : POST /api/todos
    @PostMapping
    public ResponseEntity<Long> create(@Valid @RequestBody TodoCreateRequest request) {
        Long todo = todoService.createTodo(request.getTitle(), request.getDescription());
        return ResponseEntity.ok(todo);
    }

    // 전체 조회: GET /api/todos
    @GetMapping
    public ResponseEntity<List<TodoResponse>> findAll() {
        List<Todo> todos = todoService.findAll();
        List<TodoResponse> response = todos.stream()
                .map(TodoResponse::from)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TodoResponse> findById(@RequestParam Long id) {
        Todo todo = todoService.findById(id);
        return ResponseEntity.ok(TodoResponse.from(todo));
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<Void> complete(@PathVariable Long id) {
        todoService.completeTodo(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        todoService.deleteTodo(id);
        return ResponseEntity.ok().build();
    }
}
