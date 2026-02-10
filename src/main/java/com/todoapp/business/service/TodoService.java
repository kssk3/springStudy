package com.todoapp.business.service;

import com.todoapp.common.exception.ErrorCode;
import com.todoapp.common.exception.TodoAccessDeniedException;
import com.todoapp.common.security.CurrentUserIdProvider;
import com.todoapp.common.security.SecurityUtils;
import com.todoapp.dataaccess.entity.Todo;
import com.todoapp.dataaccess.entity.User;
import com.todoapp.dataaccess.repository.TodoRepository;
import com.todoapp.dataaccess.repository.UserRepository;
import com.todoapp.pressentation.dto.request.TodoCreateRequest;
import com.todoapp.pressentation.dto.request.UpdateTodoRequest;
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
    private final UserRepository userRepository;
    private final CurrentUserIdProvider currentUserIdProvider;

    @Transactional
    public TodoResponse createTodo(TodoCreateRequest request) {
        long currentUserId = currentUserIdProvider.getCurrentUserId();

        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.TODO_NOT_FOUND.getMessage()));

        Todo todo = Todo.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .user(user)
                .build();

        Todo savedTodo = todoRepository.save(todo);
        return TodoResponse.from(savedTodo);
    }

    @Transactional
    public TodoResponse updateTodo(Long id, UpdateTodoRequest request) {
        long currentUserId = currentUserIdProvider.getCurrentUserId();

        Todo todo = todoRepository.findOwnedTodoByIdAndUserId(id, currentUserId)
                .orElseThrow(() -> new TodoAccessDeniedException());

        if(request.getTitle() != null) {
            todo.updateTitle(request.getTitle());
        }

        if(request.getDescription() != null) {
            todo.updateDescription(request.getDescription());
        }

        return TodoResponse.from(todo);
    }

    public TodoResponse findById(Long id) {
        long currentUserId = currentUserIdProvider.getCurrentUserId();

        Todo todo = todoRepository.findOwnedTodoByIdAndUserId(id, currentUserId)
                .orElseThrow(() -> new TodoAccessDeniedException());

        return TodoResponse.from(todo);
    }

    public List<TodoResponse> findAll() {
        long currentUserId = currentUserIdProvider.getCurrentUserId();

        return todoRepository.findAllByUserId(currentUserId)
                .stream()
                .map(TodoResponse::from)
                .toList();
    }

    @Transactional
    public void completeTodo(Long id) {
        long currentUserId = currentUserIdProvider.getCurrentUserId();

        Todo todo = todoRepository.findOwnedTodoByIdAndUserId(id, currentUserId)
                .orElseThrow(() -> new TodoAccessDeniedException());

        todo.complete();
    }

    @Transactional
    public void deleteTodo(Long id) {
        long currentUserId = currentUserIdProvider.getCurrentUserId();

        Todo todo = todoRepository.findOwnedTodoByIdAndUserId(id, currentUserId)
                .orElseThrow(() -> new TodoAccessDeniedException());

        todoRepository.delete(todo);
    }
}
