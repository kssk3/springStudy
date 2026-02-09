package com.todoapp.todo.integration;

import static org.assertj.core.api.Assertions.*;

import com.todoapp.business.service.TodoService;
import com.todoapp.business.service.UserService;
import com.todoapp.common.exception.TodoAccessDeniedException;
import com.todoapp.common.security.CustomUserDetailService;
import com.todoapp.common.security.CustomUserDetails;
import com.todoapp.pressentation.dto.request.SignUpRequest;
import com.todoapp.pressentation.dto.request.TodoCreateRequest;
import com.todoapp.pressentation.dto.response.TodoResponse;
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
public class TodoOwnershipIntegrationTest {

    @Autowired
    private TodoService todoService;

    @Autowired
    private UserService userService;

    @Autowired
    private CustomUserDetailService customUserDetailService;

    private Long userAId;
    private Long userBId;
    private Long todoAId;

    @BeforeEach
    void setUp() {
        // 테스트 A
        SignUpRequest userA = SignUpRequest.builder()
                .email("userA@example.com")
                .password("Password123!")
                .passwordConfirm("Password123!")
                .name("사용자A")
                .phoneNumber("010-1111-1111")
                .build();
        userAId = userService.signUp(userA).getId();

        // 사용자 B 생성
        SignUpRequest userB = SignUpRequest.builder()
                .email("userB@example.com")
                .password("Password123!")
                .passwordConfirm("Password123!")
                .name("사용자B")
                .phoneNumber("010-2222-2222")
                .build();
        userBId = userService.signUp(userB).getId();

        authenticateAs(userA.getEmail());

        TodoCreateRequest todoRequest = new TodoCreateRequest("A의 할일", "A의 설명");
        TodoResponse createdTodo = todoService.createTodo(todoRequest);
        todoAId = createdTodo.getId();
    }

    @Test
    @DisplayName("사용자 A는 자신의 Todo를 조회할 수 있다")
    void userA_CanViewOwnTodo() {
        TodoResponse todo = todoService.findById(todoAId);

        assertThat(todo.getId()).isEqualTo(todoAId);
        assertThat(todo.getTitle()).isEqualTo("A의 할일");
    }

    @Test
    @DisplayName("사용자 B는 사용자의 A의 Todo를 조회할 수 없다.")
    void userB_CanNotViewUserATodo() {
        authenticateAs("userB@example.com");

        assertThatThrownBy(() -> todoService.findById(todoAId))
                .isInstanceOf(TodoAccessDeniedException.class);
    }

    @Test
    @DisplayName("사용자 B의 Todo 목록에는 사용자 A의 Todo가 포함되지 않는다.")
    void userB_TodoListDoseNotIncludeUserATodos() {
        // given
        authenticateAs("userB@example.com");

        // when
        TodoCreateRequest todo1 = new TodoCreateRequest("testB의 할일 1", "테스트 B");
        TodoCreateRequest todo2 = new TodoCreateRequest("testB의 할일 2", "테스트 B");
        TodoResponse responseB1 = todoService.createTodo(todo1);
        TodoResponse responseB2 = todoService.createTodo(todo2);

        List<TodoResponse> todoB = todoService.findAll();

        // then
        assertThat(todoB).hasSize(2);
        assertThat(todoB).extracting("id").doesNotContain(todoAId);
        assertThat(todoB).extracting("title").doesNotContain("A의 할일");
    }

    @Test
    @DisplayName("사용자 A와 B가 각자 Todo를 생성하면 서로 격리된다")
    void userA_And_userB_TodosAreIsolated() {
        todoService.createTodo(new TodoCreateRequest("A의 할일2", "테스트 A"));

        authenticateAs("userB@example.com");
        todoService.createTodo(new TodoCreateRequest("B의 할일1", "테스트 A"));
        todoService.createTodo(new TodoCreateRequest("B의 할일2", "테스트 A"));

        List<TodoResponse> todosB = todoService.findAll();
        assertThat(todosB).hasSize(2);
        assertThat(todosB).extracting("id").doesNotContain(todoAId);
        assertThat(todosB).extracting("title")
                .containsExactlyInAnyOrder("B의 할일1", "B의 할일2");

        authenticateAs("userA@example.com");
        List<TodoResponse> todosA = todoService.findAll();
        assertThat(todosA).hasSize(2);
        assertThat(todosA).extracting("id").contains(todoAId);
        assertThat(todosA).extracting("title")
                .containsExactlyInAnyOrder("A의 할일", "A의 할일2");
    }

    private void authenticateAs(String email) {
        CustomUserDetails customUserDetails =
                (CustomUserDetails) customUserDetailService.loadUserByUsername(email);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                customUserDetails, null, customUserDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

}
