package com.todoapp.todo.integration;

import static org.assertj.core.api.Assertions.*;

import com.todoapp.business.service.UserService;
import com.todoapp.common.exception.TodoAccessDeniedException;
import com.todoapp.common.security.CustomUserDetailService;
import com.todoapp.common.security.CustomUserDetails;
import com.todoapp.dataaccess.entity.Todo;
import com.todoapp.pressentation.dto.request.SignUpRequest;
import com.todoapp.pressentation.dto.request.TodoCreateRequest;
import com.todoapp.pressentation.dto.response.TodoResponse;
import com.todoapp.dataaccess.repository.TodoRepository;
import com.todoapp.business.service.TodoService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class TodoIntegrationTest {

    @Autowired
    private TodoService todoService;

    @Autowired
    private UserService userService;

    @Autowired
    private CustomUserDetailService customUserDetailService;

    @Autowired
    private TodoRepository todoRepository;

    @BeforeEach
    void setup() {
        SignUpRequest user = SignUpRequest.builder()
                .email("test123@gmail.com")
                .password("Password123!")
                .passwordConfirm("Password123!")
                .name("강두기")
                .phoneNumber("010-1234-1234")
                .build();

        userService.signUp(user);
        authenticateAs(user.getEmail());
    }

    @Test
    @DisplayName("정상적인 요청으로 Todo 생성 성공")
    void createTodo_WithValidRequest_Success() {
        // given
        TodoCreateRequest request = new TodoCreateRequest("스프링 공부", "JPA 학습하기");

        // when
        TodoResponse response = todoService.createTodo(request);

        // then Service 검증
        assertThat(response.getId()).isNotNull();
        assertThat(response.getTitle()).isEqualTo("스프링 공부");
        assertThat(response.getDescription()).isEqualTo("JPA 학습하기");
        assertThat(response.isCompleted()).isFalse();

        // then DB 저장 검증
        Todo savedTodo = todoRepository.findById(response.getId()).orElseThrow();
        assertThat(savedTodo.getTitle()).isEqualTo(response.getTitle());
        assertThat(savedTodo.getDescription()).isEqualTo(response.getDescription());
        assertThat(savedTodo.isCompleted()).isFalse();
    }

    @Test
    @DisplayName("설명 없이 Todo 생성 성공")
    void createTodo_WithOutDescription_Success() {
        // given
        TodoCreateRequest request = new TodoCreateRequest("제목만 있는 Todo", null);

        // when
        TodoResponse response = todoService.createTodo(request);

        // then Service 검증
        assertThat(response.getTitle()).isEqualTo("제목만 있는 Todo");
        assertThat(response.getDescription()).isNull();

        // then DB 저장 검증
        Todo savedTodo = todoRepository.findById(response.getId()).orElseThrow();
        assertThat(savedTodo.getTitle()).isEqualTo(response.getTitle());
        assertThat(savedTodo.getDescription()).isNull();
    }

    @Test
    @DisplayName("ID로 Todo 조회 성공")
    void findById_WithExistingId_Success() {
        // given
        TodoCreateRequest request = new TodoCreateRequest("테스트 Todo", "설명");
        TodoResponse created = todoService.createTodo(request);

        // when
        Todo savedTodo = todoRepository.findById(created.getId()).orElseThrow();

        // then DB 저장 검증
        assertThat(savedTodo.getId()).isEqualTo(created.getId());
        assertThat(savedTodo.getTitle()).isEqualTo(created.getTitle());
        assertThat(savedTodo.getDescription()).isEqualTo(created.getDescription());
    }

    @Test
    @DisplayName("존재하지 않는 ID 조회 시 예외 발생")
    void findById_NotExistingId_ThrowsException() {
        Long nonExistingId = 999L;

        assertThatThrownBy(() -> todoService.findById(nonExistingId))
                .isInstanceOf(TodoAccessDeniedException.class);
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

        // then Service 검증
        assertThat(todos).hasSize(3);
        assertThat(todos).extracting("title")
                .containsExactlyInAnyOrder("Todo 1", "Todo 3", "Todo 2");

        // then DB 데이터 확인
        List<Todo> savedTodos = todoRepository.findAll();
        assertThat(savedTodos).hasSize(3);
    }

    @Test
    @DisplayName("빈 목록 조회 - 빈 리스트 반환")
    void findAll_WithNoTodos_ReturnsEmptyList() {
        // when
        List<TodoResponse> todos = todoService.findAll();

        // then
        assertThat(todos).isEmpty();
    }

    @Test
    @DisplayName("Todo 완료 처리 성공")
    void createTodo_Success() {
        // given
        TodoResponse createTodo = todoService.createTodo(new TodoCreateRequest("테스트", null));
        assertThat(createTodo.isCompleted()).isFalse();

        // when
        todoService.completeTodo(createTodo.getId());

        // then
        TodoResponse savedTodo = todoService.findById(createTodo.getId());
        assertThat(savedTodo.isCompleted()).isTrue();

        // then DB 확인
        Todo dbTodos = todoRepository.findById(createTodo.getId()).orElseThrow();
        assertThat(dbTodos.isCompleted()).isTrue();
    }

    @Test
    @DisplayName("존재하지 않은 Todo 완료 시 TodoAccessDeniedException 발생")
    void createTodo_WithNonExistingId_ThrowsException() {
        final Long nonExistingId = 999L;

        assertThatThrownBy(() -> todoService.completeTodo(nonExistingId))
                .isInstanceOf(TodoAccessDeniedException.class);
    }

    @Test
    @DisplayName("Todo 삭제 성공")
    void deleteTodo_Success() {
        TodoResponse createTodo = todoService.createTodo(new TodoCreateRequest("테스트 성공!", null));

        todoService.deleteTodo(createTodo.getId());

        assertThatThrownBy(() -> todoService.findById(createTodo.getId()))
                .isInstanceOf(TodoAccessDeniedException.class);
    }

    @Test
    @DisplayName("Todo 생성 -> 완료 -> 조회 전체 플로우")
    void todo_FullFlow_CreateCompleted_Success() {
        // 생성 & 검증
        TodoResponse createTodo = todoService.createTodo(new TodoCreateRequest("테스트 Todo", null));
        assertThat(createTodo.isCompleted()).isFalse();

        // DB 즉시 검증
        Todo dbTodo = todoRepository.findById(createTodo.getId()).orElseThrow();
        assertThat(dbTodo.getTitle()).isEqualTo(createTodo.getTitle());

        // 완료 처리 & 검증
        todoService.completeTodo(createTodo.getId());
        TodoResponse response = todoService.findById(createTodo.getId());
        assertThat(response.isCompleted()).isTrue();

        Todo dbCompleted = todoRepository.findById(createTodo.getId()).orElseThrow();
        assertThat(dbCompleted.isCompleted()).isTrue();

        todoService.deleteTodo(createTodo.getId());
        assertThatThrownBy(() -> todoService.findById(createTodo.getId()))
                .isInstanceOf(TodoAccessDeniedException.class);

        assertThat(todoRepository.findById(createTodo.getId())).isEmpty();
    }

    private void authenticateAs(String email) {
        CustomUserDetails userDetails =
                (CustomUserDetails) customUserDetailService.loadUserByUsername(email);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
