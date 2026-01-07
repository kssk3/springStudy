package test;

import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;

    // 생성
    @Transactional
    public Long createTodo(String title, String description) {
        Todo todo = new Todo(title, description);
        Todo saved = todoRepository.save(todo);
        return saved.getId();
    }

    public Todo findById(Long id) {
        return todoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Todo not found " + id));
    }

    public List<Todo> findAll() {
        return todoRepository.findAll();
    }

    @Transactional
    public void completeTodo(Long id) {
        Todo todo = findById(id);
        todo.complete();
    }

    @Transactional
    public void deleteTodo(Long id) {
        Todo todo = findById(id);
        todoRepository.deleteById(id);
    }
}
