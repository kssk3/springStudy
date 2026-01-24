package com.todoapp.business.service;

import com.todoapp.dataaccess.entity.Todo;
import com.todoapp.dataaccess.repository.TodoRepository;
import com.todoapp.pressentation.dto.request.TodoCreateRequest;
import com.todoapp.pressentation.dto.response.TodoResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;

    @Transactional
    public TodoResponse createTodo(TodoCreateRequest request) {
        Todo todo = new Todo(request.getTitle(), request.getDescription());

        Todo savedTodo = todoRepository.save(todo);
        return TodoResponse.from(savedTodo);
    }

    public TodoResponse findById(Long id) {
        Todo todo = todoRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Todo not found"));

        return TodoResponse.from(todo);
    }
    
    public List<TodoResponse> findAll() {
        return todoRepository.findAll().stream()
                .map(TodoResponse::from)
                .toList();
    }

    @Transactional
    public void completeTodo(Long id) {
        Todo todo = todoRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Todo not found"));

        todo.complete();
    }

    @Transactional
    public void deleteTodo(Long id) {
        Todo todo = todoRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Todo not found"));

        todoRepository.delete(todo);
    }
}
