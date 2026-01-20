package com.todoapp;

import static org.assertj.core.api.Assertions.*;

import com.todoapp.domain.Todo;
import com.todoapp.dto.request.TodoCreateRequest;
import com.todoapp.dto.response.TodoResponse;
import com.todoapp.repository.TodoRepository;
import com.todoapp.service.TodoService;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class TodoIntegrationTest {

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private TodoService todoService;

    @Test
    @DisplayName("정상적인 요청으로 Todo 생성 성공")
    void createTodo_WithValidRequest_Success() {
        // given
        TodoCreateRequest request = new TodoCreateRequest("스프링 공부", "JPA 학습하기");

        // when
        TodoResponse response = todoService.createTodo(request);

        // then
        assertThat(response.getId()).isNotNull();
        assertThat(response.getTitle()).isEqualTo("스프링 공부");
        assertThat(response.isCompleted()).isFalse();

        // DB에 실제 저장되는지 확인
        Todo savedTodo = todoRepository.findById(response.getId()).orElseThrow();
        assertThat(savedTodo.getTitle()).isEqualTo(response.getTitle());
    }

    @Test
    @DisplayName("설명 없이 Todo 생성 성공")
    void createTodo_WithOutDescription_Success() {
        // given
        TodoCreateRequest request = new TodoCreateRequest("제목만 있는 Todo", null);

        // when
        TodoResponse response = todoService.createTodo(request);

        // then
        assertThat(response.getTitle()).isEqualTo("제목만 있는 Todo");
        assertThat(response.getDescription()).isNull();
    }


    @Test
    @DisplayName("ID로 Todo 조회 성공")
    void findById_WithExistingId_Success() {
        // given
        TodoCreateRequest request = new TodoCreateRequest("테스트 Todo", "설명");
        TodoResponse created = todoService.createTodo(request);

        // when
        TodoResponse foundId = todoService.findById(created.getId());

        // then
        assertThat(foundId.getId()).isEqualTo(created.getId());
        assertThat(foundId.getTitle()).isEqualTo(created.getTitle());
    }

    @Test
    @DisplayName("존재하지 않는 ID 조회 시 예외 발생")
    void findById_NotExistingId_ThrowsException() {
        Long nonExistingId = 999L;

        assertThatThrownBy(() -> todoService.findById(nonExistingId))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("전체 Todo 목록 조회")
    void findAll_ReturnsAllTodos() {
        // given
        todoService.createTodo(new TodoCreateRequest("Todo 1", "설명 1"));
        todoService.createTodo(new TodoCreateRequest("Todo 2", "설명 2"));
        todoService.createTodo(new TodoCreateRequest("Todo 3", "설명 3"));

        // when
        List<TodoResponse> todos = todoService.findAll();

        // then
        assertThat(todos).hasSize(3);
        assertThat(todos).extracting("title")
                .containsExactlyInAnyOrder("Todo 1", "Todo 3", "Todo 2");
    }

    @Test
    @DisplayName("Todo 완료 처리 성공")
    void createTodo_Success() {
        // given
        TodoResponse createTodo = todoService.createTodo(new TodoCreateRequest("테스트", null));

        // when
        todoService.completeTodo(createTodo.getId());

        // then
        Todo todo = todoRepository.findById(createTodo.getId()).orElseThrow();
        assertThat(todo.isCompleted()).isTrue();
    }

    @Test
    @DisplayName("존재하지 않은 Todo 완료 시 예외 발생")
    void createTodo_WithNonExistingId_ThrowsException() {
        final Long nonExistingId = 999L;

        assertThatThrownBy(() -> todoService.completeTodo(nonExistingId))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Todo 삭제 성공")
    void deleteTodo_Success() {
        // given
        TodoResponse createTodo = todoService.createTodo(new TodoCreateRequest("테스트 성공!", null));

        // when
        todoService.deleteTodo(createTodo.getId());

        // then
        assertThatThrownBy(() -> todoService.findById(createTodo.getId()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Todo 생성 -> 완료 -> 조회 전체 플로우")
    void todo_FullFlow_CreateCompleted_Success() {
        TodoResponse createTodo = todoService.createTodo(new TodoCreateRequest("테스트 Todo", null));

        todoService.completeTodo(createTodo.getId());

        TodoResponse response = todoService.findById(createTodo.getId());
        assertThat(response.isCompleted()).isTrue();
        assertThat(response.getTitle()).isEqualTo("테스트 Todo");
    }
}
